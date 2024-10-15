package tools.important.tankswars.building.tank

import tanks.Game
import tanks.IGameObject
import tanks.Movable
import tanks.Team
import tanks.bullet.Bullet
import tanks.gui.screen.ScreenPartyHost
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
import tools.important.tankswars.event.to_client.EventBuildingWasDestroyed
import tools.important.tankswars.event.to_client.EventBuildingWasSilentlyCaptured
import tools.important.tankswars.util.getTeamColorOrGray
import tools.important.tankswars.util.getTeamNameFromDestroyer
import tools.important.tankswars.util.sendCaptureMessage
import tools.important.tankswars.util.sendDestroyMessage

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
) { // fixme: some weird bug i can't reproduce with capturable buildings dying on clients
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
        News.sendDestroyMessage(this, destroyer)

        if (!ScreenPartyHost.isServer) return

        for (connection in ScreenPartyHost.server.connections) {
            val (r, g, b) = getTeamColorOrGray(team)
            val (dr, dg, db) = getTeamColorOrGray(destroyer?.team)
            connection.events.add(EventBuildingWasDestroyed(
                name,
                r.toInt(),
                g.toInt(),
                b.toInt(),
                getTeamNameFromDestroyer(destroyer),
                dr.toInt(),
                dg.toInt(),
                db.toInt(),
                Team.isAllied(this, connection.player.tank)
            ))
        }
    }

    override fun damage(amount: Double, source: IGameObject?): Boolean {
        val dead = super.damage(amount, source)

        val sourceTank = when (source) {
            is Bullet -> source.tank
            is Explosion -> source.tank
            is Mine -> source.tank
            is Tank -> source

            else -> null
        }

        if (type.captureProperties == null) {
            if (dead) sendDestroyMessage(sourceTank)
            return dead
        }

        if (source !is Movable?) return false

        if (dead || team == null) {
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
        destroy = false
        team = capturingTank?.team
        health = if (team != null) type.health else 0.01

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