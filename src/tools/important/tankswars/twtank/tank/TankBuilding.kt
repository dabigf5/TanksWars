package tools.important.tankswars.twtank.tank

import basewindow.Color
import tanks.Game
import tanks.GameObject
import tanks.Movable
import tanks.bullet.Bullet
import tanks.network.event.EventTankRemove
import tanks.network.event.EventTankUpdateHealth
import tanks.tank.Explosion
import tanks.tank.Mine
import tanks.tank.Tank
import tools.important.tankswars.twtank.TwTankType
import tools.important.tankswars.core.News
import tools.important.tankswars.core.SharedSystem
import tools.important.tankswars.event.NIL_ID
import tools.important.tankswars.event.to_client.EventBuildingWasCaptured
import tools.important.tankswars.event.to_client.EventBuildingWasSilentlyCaptured
import tools.important.tankswars.twtank.TwTank
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
 * @see TwTankType
 */
abstract class TankBuilding(name: String, x: Double, y: Double, angle: Double) : TwTank(
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
    val type: TwTankType = TwTankType.getTankTypeFromClass(javaClass)!!

    val startPosX = posX
    val startPosY = posY

    init {
        description = type.description
        health = type.buildingProperties!!.health
        baseHealth = type.buildingProperties.health
        enableMineLaying = false

        enableMovement = false
        turretLength = 0.0
        spawnedInitialCount = 0

        emblemColor = Color(255.0, 255.0, 255.0)
    }

    fun sendDestroyMessage(destroyer: Tank?) {
        News.broadcastDestroyMessage(this, destroyer)
    }

    open fun isEligibleToCapture(capturer: Tank): Boolean {
        return distanceBetween(capturer, this) < size * 2.0
    }

    override fun damage(amount: Double, source: GameObject?): Boolean {
        super.damage(amount, source)

        val deadForReal = isDeadForReal

        val sourceTank = when (source) {
            is Bullet -> source.tank
            is Explosion -> source.tank
            is Mine -> source.tank
            is Tank -> source

            else -> null
        }

        if (type.buildingProperties!!.captureProperties == null) {
            if (deadForReal) {
                sendDestroyMessage(sourceTank)
                twOnDeath()
            }
            return deadForReal
        }

        if (source !is Movable?) {
            health = type.buildingProperties.health
            destroy = false
            return false
        }

        if (deadForReal || team == null) {
            if (sourceTank != null && isEligibleToCapture(sourceTank)) {
                capture(sourceTank)
            } else {
                voidDeath(health + amount)
            }
        }

        return false
    }

    override fun update() {
        val typeSpawnChance = type.buildingProperties!!.spawnChance

        if (typeSpawnChance != null) {
            spawnChance = if (team != null) typeSpawnChance else 0.0
        }

        if (destroy && destroyTimer <= 0) {
            twOnDeath()
        }

        super.update()
    }

    private fun voidDeath(newHealth: Double) {
        health = newHealth
        destroy = false
        Game.eventsOut.add(EventTankUpdateHealth(this))

        val eventsOut = Game.eventsOut
        eventsOut.add(EventTankUpdateHealth(this))

        for (event in eventsOut)
            if (event is EventTankRemove && event.tank == networkID) eventsOut.remove(event)
    }

    private fun serversideCapture(capturingTank: Tank?) {
        voidDeath(type.buildingProperties!!.health)

        team = capturingTank?.team

        SharedSystem.setProperty(this, "timeSinceCapture", 0.0)
    }

    fun silentCapture(capturingTank: Tank?) {
        serversideCapture(capturingTank)
        Game.eventsOut.add(EventBuildingWasSilentlyCaptured(this.networkID, capturingTank?.networkID ?: NIL_ID))
    }

    open fun capture(capturingTank: Tank?) {
        News.sendCaptureMessage(this, capturingTank)
        type.buildingProperties!!.captureProperties?.onSharedCapture?.invoke(this)

        serversideCapture(capturingTank)

        val eventsOut = Game.eventsOut
        eventsOut.add(EventBuildingWasCaptured(this.networkID, capturingTank?.networkID ?: NIL_ID))
    }
}