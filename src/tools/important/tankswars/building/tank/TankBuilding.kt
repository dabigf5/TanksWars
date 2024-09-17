package tools.important.tankswars.building.tank

import tanks.Game
import tanks.IGameObject
import tanks.Movable
import tanks.bullet.Bullet
import tanks.network.event.EventTankRemove
import tanks.network.event.EventTankUpdateHealth
import tanks.tank.Explosion
import tanks.tank.Mine
import tanks.tank.Tank
import tanks.tank.TankAIControlled
import tools.important.tankswars.building.BuildingType
import tools.important.tankswars.event.EventBuildingWasCaptured

/**
 * TankBuilding is the base class of all server-side buildings.
 * As such, it and its subclasses should only contain things that are exclusive to the server.
 *
 * @see BuildingType
 */
abstract class TankBuilding(name: String, x: Double, y: Double, angle: Double) : TankAIControlled(
    name,
    x,
    y,
    Game.tile_size,
    100.0,
    100.0,
    100.0,
    angle,
    ShootAI.none,
) {
    val type: BuildingType = BuildingType.getBuildingTypeFromClass(javaClass)!!
    init {
        description = type.description
        health = type.health
        enableMineLaying = false

        enableMovement = false
        turretLength = 0.0
    }

    override fun damage(amount: Double, source: IGameObject?): Boolean {
        val dead = super.damage(amount, source)
        if (!type.capturable) return dead

        if (source !is Movable) return dead
        val sourceTank = when(source) {
            is Bullet -> source.tank
            is Explosion -> source.tank
            is Mine -> source.tank
            is Tank -> source

            else -> error("WTF is this damage source")
        }

        if (dead) {
            capture(sourceTank)
        }

        return false
    }

    fun capture(capturingTank: Tank?) {
        destroy = false
        health = type.health

        val eventsOut = Game.eventsOut
        eventsOut.add(EventTankUpdateHealth(this))

        for (event in eventsOut)
            if (event is EventTankRemove && event.tank == networkID) eventsOut.remove(event)

        val event = EventBuildingWasCaptured(this, capturingTank)
        event.execute()

        eventsOut.add(event)
    }
}