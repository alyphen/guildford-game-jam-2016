package com.seventh_root.guildfordgamejam.screen

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType.Filled
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType.Line
import com.badlogic.gdx.math.Vector3
import com.seventh_root.guildfordgamejam.GuildfordGameJam
import com.seventh_root.guildfordgamejam.component.*
import com.seventh_root.guildfordgamejam.level.Level
import com.seventh_root.guildfordgamejam.system.*

class MainScreen(game: GuildfordGameJam): ScreenAdapter() {

    val engine = Engine()
    val shapeRenderer = ShapeRenderer()
    val playerFamily: Family = Family.all(PlayerComponent::class.java).get()
    val grappleFamily: Family = Family.all(GrappleComponent::class.java).get()
    val finishFamily: Family = Family.all(FinishComponent::class.java).get()
    val finishEffectFamily: Family = Family.all(FinishEffectComponent::class.java).get()
    val camera = OrthographicCamera()
    val orbitSound: Sound = Gdx.audio.newSound(Gdx.files.internal("orbit.ogg"))
    val pluck1Sound: Sound = Gdx.audio.newSound(Gdx.files.internal("pluck1.ogg"))
    val pluck2Sound: Sound = Gdx.audio.newSound(Gdx.files.internal("pluck2.ogg"))
    val pluck3Sound: Sound = Gdx.audio.newSound(Gdx.files.internal("pluck3.ogg"))
    val pluck4Sound: Sound = Gdx.audio.newSound(Gdx.files.internal("pluck4.ogg"))
    val popSound: Sound = Gdx.audio.newSound(Gdx.files.internal("pop.ogg"))
    val levelCompletePlucks: Sound = Gdx.audio.newSound(Gdx.files.internal("level_complete_plucks.ogg"))
    val font = BitmapFont(Gdx.files.internal("m5x7.fnt"))
    val spriteBatch = SpriteBatch()

    init {
        camera.setToOrtho(true)
        engine.addSystem(MovementSystem())
        engine.addSystem(FrictionSystem())
        engine.addSystem(GravitySystem())
        engine.addSystem(PullSystem())
        engine.addSystem(PlayerSizeSystem())
        engine.addSystem(PlayerColorSystem())
        engine.addSystem(FinishSystem(game))
        engine.addSystem(RadiusScalingSystem())
        engine.addSystem(ColorCollectionSystem())
        engine.addSystem(PlayerSoundSystem(game))
        engine.addSystem(TimerSystem())
    }

    override fun show() {
        Gdx.input.inputProcessor = object: InputAdapter() {
            override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
                engine.getEntitiesFor(playerFamily).forEach { player ->
                    if (velocity.get(player).x == 0F && velocity.get(player).y == 0F) {
                        val startX = position.get(player).x
                        val startY = position.get(player).y
                        val endX = Gdx.input.x.toFloat() + camera.position.x - (Gdx.graphics.width / 2)
                        val endY = Gdx.input.y.toFloat() + camera.position.y - (Gdx.graphics.height / 2)
                        val h2 = 8F
                        val o1 = endY - startY
                        val a1 = endX - startX
                        val theta = Math.atan((o1 / a1).toDouble()) + if (endX < startX) Math.PI else 0.0
                        val a2 = Math.cos(theta) * h2
                        val o2 = Math.sin(theta) * h2
                        velocity.get(player).x = a2.toFloat()
                        velocity.get(player).y = o2.toFloat()
                        orbitSound.play()
                    }
                }
                return true
            }
        }
    }

    override fun hide() {
        Gdx.input.inputProcessor = null
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0.toFloat(), 0.toFloat(), 0.toFloat(), 0.toFloat())
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        engine.update(delta)
        val playerEntity = engine.getEntitiesFor(playerFamily).first()
        camera.position.set(Vector3((camera.position.x * (15F/16F)) + (position.get(playerEntity).x * (1F/16F)), (camera.position.y * (15F/16F)) + (position.get(playerEntity).y * (1F/16F)), 0.toFloat()))
        camera.update()
        shapeRenderer.projectionMatrix = camera.combined
        shapeRenderer.color = color.get(playerEntity).color
        shapeRenderer.begin(Line)
        shapeRenderer.circle(position.get(playerEntity).x, position.get(playerEntity).y, radius.get(playerEntity).radius)
        shapeRenderer.end()
        shapeRenderer.begin(Line)
        val startX = position.get(playerEntity).x
        val startY = position.get(playerEntity).y
        val endX = Gdx.input.x.toFloat() + camera.position.x - (Gdx.graphics.width / 2)
        val endY = Gdx.input.y.toFloat() + camera.position.y - (Gdx.graphics.height / 2)
        val h2 = 64F
        val o1 = endY - startY
        val a1 = endX - startX
        val theta = Math.atan((o1 / a1).toDouble()) + if (endX < startX) Math.PI else 0.0
        val a2 = Math.cos(theta) * h2
        val o2 = Math.sin(theta) * h2
        val x2 = startX + a2
        val y2 = startY + o2
        shapeRenderer.line(startX, startY, x2.toFloat(), y2.toFloat())
        shapeRenderer.end()
        engine.getEntitiesFor(grappleFamily).forEach { grappleEntity ->
            if (collectedColors.get(playerEntity).colors.contains(color.get(grappleEntity).color)) {
                shapeRenderer.color = Color.WHITE
            } else {
                shapeRenderer.color = color.get(grappleEntity).color
            }
            shapeRenderer.begin(Filled)
            shapeRenderer.circle(position.get(grappleEntity).x, position.get(grappleEntity).y, radius.get(grappleEntity).radius)
            shapeRenderer.end()
        }
        engine.getEntitiesFor(finishFamily).forEach { finishEntity ->
            shapeRenderer.color = color.get(finishEntity).color
            shapeRenderer.begin(Line)
            shapeRenderer.circle(position.get(finishEntity).x, position.get(finishEntity).y, radius.get(finishEntity).radius)
            shapeRenderer.end()
        }
        engine.getEntitiesFor(finishEffectFamily).forEach { finishEffectEntity ->
            shapeRenderer.color = color.get(finishEffectEntity).color
            shapeRenderer.begin(Line)
            shapeRenderer.circle(position.get(finishEffectEntity).x, position.get(finishEffectEntity).y, radius.get(finishEffectEntity).radius)
            shapeRenderer.end()
        }
        spriteBatch.begin()
        font.draw(spriteBatch, "${collectedColors.get(playerEntity).colors.size}/${engine.getEntitiesFor(grappleFamily).filter { grapple -> color.get(grapple).color != Color.WHITE }.size}", 16F, 32F)
        font.draw(spriteBatch, String.format("%.2f", timer.get(playerEntity).time), 16F, 64F)
        spriteBatch.end()
    }

    override fun dispose() {
        shapeRenderer.dispose()
        orbitSound.dispose()
        pluck1Sound.dispose()
        pluck2Sound.dispose()
        pluck3Sound.dispose()
        pluck4Sound.dispose()
        popSound.dispose()
        levelCompletePlucks.dispose()
        font.dispose()
        spriteBatch.dispose()
    }

    fun displayLevel(level: Level) {
        engine.removeAllEntities()
        level.entities.forEach { entity -> engine.addEntity(entity) }
    }

}