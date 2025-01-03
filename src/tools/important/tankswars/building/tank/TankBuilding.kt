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
import tools.important.tankswars.TanksWars
import tools.important.tankswars.building.BuildingType
import tools.important.tankswars.core.News
import tools.important.tankswars.event.to_client.EventBuildingWasCaptured
import tools.important.tankswars.event.to_client.EventBuildingWasSilentlyCaptured
import tools.important.tankswars.util.broadcastDestroyMessage
import tools.important.tankswars.util.isDeadForReal
import tools.important.tankswars.util.sendCaptureMessage

/**
 * TankBuilding is the base class of all server-side buildings.
 * As such, it and its subclasses should only contain things that are exclusive to the server.
 *
 * However, the exception to this rule is the companion object,
 * which can contain any constant information related to the building.
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
    // fixme: some weird bug i can't reproduce with capturable buildings dying on clients but not the server
    val type: BuildingType = BuildingType.getBuildingTypeFromClass(javaClass)!!

    init {
        description = type.description
        health = type.health
        enableMineLaying = false

        enableMovement = false
        turretLength = 0.0
        spawnedInitialCount = 0

        emblemR = 255.0
        emblemG = 255.0
        emblemB = 255.0
    }

    fun sendDestroyMessage(destroyer: Tank?) {
        News.broadcastDestroyMessage(this, destroyer)
    }

    override fun damage(amount: Double, source: IGameObject?): Boolean {
        super.damage(amount, source)

        val deadForReal = isDeadForReal

        val sourceTank = when (source) {
            is Bullet -> source.tank
            is Explosion -> source.tank
            is Mine -> source.tank
            is Tank -> source

            else -> null
        }

        if (type.captureProperties == null) {
            if (deadForReal)
                sendDestroyMessage(sourceTank)
            return deadForReal
        }

        if (source !is Movable?) {
            health = type.health
            destroy = false
            return false
        }

        if (deadForReal || team == null) {
            capture(sourceTank)
        }

        return false
    }

    override fun update() {
        val typeSpawnChance = type.spawnChance

        if (typeSpawnChance != null) {
            spawnChance = if (team != null) typeSpawnChance else 0.0
        }

        super.update()
    }

    private fun serversideCapture(capturingTank: Tank?) {
        health = type.health
        destroy = false

        team = capturingTank?.team

        TanksWars.buildingProperties.putIfAbsent(this, mutableMapOf())
        TanksWars.buildingProperties[this]!!["timeSinceCapture"] = 0.0

        val eventsOut = Game.eventsOut
        eventsOut.add(EventTankUpdateHealth(this))

        for (event in eventsOut)
            if (event is EventTankRemove && event.tank == networkID) eventsOut.remove(event)
    }

    fun silentCapture(capturingTank: Tank?) {
        serversideCapture(capturingTank)
        Game.eventsOut.add(EventBuildingWasSilentlyCaptured(this, capturingTank))
    }

    open fun capture(capturingTank: Tank?) {
        News.sendCaptureMessage(this, capturingTank)
        type.captureProperties?.onSharedCapture?.invoke(this)

        serversideCapture(capturingTank)

        val eventsOut = Game.eventsOut
        eventsOut.add(EventBuildingWasCaptured(this, capturingTank))
    }
}