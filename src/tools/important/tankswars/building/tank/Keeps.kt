package tools.important.tankswars.building.tank

import tanks.Drawing
import tanks.Game
import tanks.Panel
import tanks.network.event.EventTankRemove
import tanks.tank.Tank
import tools.important.tankswars.TanksWars
import tools.important.tankswars.core.flee
import tools.important.tankswars.event.to_client.EventTankEmblemUpdate
import tools.important.tankswars.tank.TankSoldierDefender
import tools.important.tankswars.util.getTeamColorOrGray

open class TankKeep(name: String, x: Double, y: Double, angle: Double) : TankBuilding(
    name,
    x,
    y,
    angle,
) {
    companion object {
        const val KEEP_SQUARE_SIZE = Game.tile_size * 9
        const val MAX_TIME_SINCE_CAPTURE = 150.0
    }

    init {
        spawnedTankEntries.add(SpawnedTankEntry(TankSoldierDefender("tw_soldierdefender", 0.0, 0.0, 0.0), 1.0))

        spawnedMaxCount = 10

        emblem = "emblems/square.png"
    }

    override fun capture(capturingTank: Tank?) {
        super.capture(capturingTank)
        for (defender in spawnedTanks) {
            defender.destroy = true
            Game.eventsOut.add(EventTankRemove(defender, true))
        }
    }
}

class TankKeepBase(name: String, x: Double, y: Double, angle: Double) : TankKeep(
    name,
    x,
    y,
    angle,
) {
    init {
        spawnedMaxCount = 15

        emblem = "emblems/star.png"
    }

    /**
     * Whether or not this keep will cause its team to flee upon being captured.
     */
    var liability = true
        set(v) {
            field = v
            emblem = if (v) "emblems/star.png" else "emblems/square.png"
            Game.eventsOut.add(EventTankEmblemUpdate(this, emblem, emblemR, emblemG, emblemB))
        }

    override fun capture(capturingTank: Tank?) {
        if (!liability) {
            super.capture(capturingTank)
            return
        }

        liability = false

        if (team != null) flee(team, this)

        super.capture(capturingTank)
    }
}

val keepSharedDraw = fun(tank: Tank) {
    val drawing = Drawing.drawing

    val (r, g, b) = getTeamColorOrGray(tank.team)
    drawing.setColor(r, g, b, 20.0)
    drawing.fillRect(tank.posX, tank.posY, TankKeep.KEEP_SQUARE_SIZE, TankKeep.KEEP_SQUARE_SIZE)

    if (tank.team == null) return

    val properties = TanksWars.buildingProperties[tank] ?: return
    val timeSinceCapture = properties["timeSinceCapture"] as Double? ?: return

    val circleSize = (timeSinceCapture / TankKeep.MAX_TIME_SINCE_CAPTURE) * TankKeep.KEEP_SQUARE_SIZE
    val circleOpacity = 200.0 - ((timeSinceCapture / TankKeep.MAX_TIME_SINCE_CAPTURE) * 200.0)


    drawing.setColor(r, g, b, circleOpacity)
    drawing.fillOval(tank.posX, tank.posY, circleSize, circleSize)
}

val keepSharedUpdate = fun(tank: Tank) {
    val properties = TanksWars.buildingProperties[tank] ?: return

    properties["timeSinceCapture"] as Double? ?: return

    val newTimeSinceCapture = (properties["timeSinceCapture"] as Double) + Panel.frameFrequency
    if (newTimeSinceCapture > TankKeep.MAX_TIME_SINCE_CAPTURE) {
        properties.remove("timeSinceCapture")
        return
    }

    properties["timeSinceCapture"] = newTimeSinceCapture
}