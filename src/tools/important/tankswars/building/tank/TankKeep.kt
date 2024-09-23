package tools.important.tankswars.building.tank

import tanks.Drawing
import tanks.Game
import tanks.Panel
import tanks.tank.Tank
import tools.important.tankswars.TanksWars
import tools.important.tankswars.tank.TankSoldierDefender

class TankKeep(name: String, x: Double, y: Double, angle: Double) : TankBuilding(
    name,
    x,
    y,
    angle,
) {
    companion object {
        const val KEEP_SQUARE_SIZE = Game.tile_size * 7
        const val MAX_TIME_SINCE_CAPTURE = 150.0
    }

    init {
        spawnedTankEntries.add(SpawnedTankEntry(TankSoldierDefender("tw_soldierdefender", 0.0, 0.0, 0.0), 1.0))

        spawnedMaxCount = 10
    }

    override fun capture(capturingTank: Tank?) {
        super.capture(capturingTank)

        for (defender in spawnedTanks) {
            defender.destroy = true
        }
    }
}

val keepSharedDraw = fun(tank: Tank) {
    val drawing = Drawing.drawing

    val team = tank.team
    drawing.setColor(team.teamColorR, team.teamColorG, team.teamColorB, 64.0)
    drawing.fillRect(tank.posX, tank.posY, TankKeep.KEEP_SQUARE_SIZE, TankKeep.KEEP_SQUARE_SIZE)

    val properties = TanksWars.buildingProperties[tank] ?: return
    val timeSinceCapture = properties["timeSinceCapture"] as Double? ?: return

    val circleSize = (timeSinceCapture / TankKeep.MAX_TIME_SINCE_CAPTURE) * TankKeep.KEEP_SQUARE_SIZE
    val circleOpacity = 200.0 - ((timeSinceCapture / TankKeep.MAX_TIME_SINCE_CAPTURE) * 200.0)


    drawing.setColor(team.teamColorR, team.teamColorG, team.teamColorB, circleOpacity)
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