package com.seventh_root.guildfordgamejam.component

import com.badlogic.ashley.core.ComponentMapper

val position: ComponentMapper<PositionComponent> = ComponentMapper.getFor(PositionComponent::class.java)
val velocity: ComponentMapper<VelocityComponent> = ComponentMapper.getFor(VelocityComponent::class.java)
val friction: ComponentMapper<FrictionComponent> = ComponentMapper.getFor(FrictionComponent::class.java)
val gravity: ComponentMapper<GravityComponent> = ComponentMapper.getFor(GravityComponent::class.java)
val player: ComponentMapper<PlayerComponent> = ComponentMapper.getFor(PlayerComponent::class.java)
//val grapple: ComponentMapper<GrappleComponent> = ComponentMapper.getFor(GrappleComponent::class.java)
val color: ComponentMapper<ColorComponent> = ComponentMapper.getFor(ColorComponent::class.java)
val radius: ComponentMapper<RadiusComponent> = ComponentMapper.getFor(RadiusComponent::class.java)
val texture: ComponentMapper<TextureComponent> = ComponentMapper.getFor(TextureComponent::class.java)
val level: ComponentMapper<LevelComponent> = ComponentMapper.getFor(LevelComponent::class.java)
val collectedColors: ComponentMapper<CollectedColorsComponent> = ComponentMapper.getFor(CollectedColorsComponent::class.java)
val radiusScaling: ComponentMapper<RadiusScalingComponent> = ComponentMapper.getFor(RadiusScalingComponent::class.java)
val finish: ComponentMapper<FinishComponent> = ComponentMapper.getFor(FinishComponent::class.java)
val timer: ComponentMapper<TimerComponent> = ComponentMapper.getFor(TimerComponent::class.java)
