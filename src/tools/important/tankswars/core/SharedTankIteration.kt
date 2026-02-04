package tools.important.tankswars.core

import tanks.Drawing
import tanks.Game
import tanks.tank.Tank
import tools.important.tankswars.building.TwTankType
import tools.important.tankswars.util.component1
import tools.important.tankswars.util.component2
import tools.important.tankswars.util.component3
import tools.important.tankswars.util.getTeamColorOrGray

fun sharedPreUpdateTanks() {
    for (movable in Game.movables) {
        if (movable !is Tank) continue

        val tankType = TwTankType.getTankTypeFromName(movable.name) ?: continue

        if (tankType.buildingProperties?.stationary == true) {
            movable.vX = 0.0
            movable.vY = 0.0
            movable.orientation = 0.0

            movable.posX = movable.lastPosX
            movable.posY = movable.lastPosY
        }

        tankType.onSharedPreUpdate?.invoke(movable)
    }
}

fun sharedUpdateTanks() {
    for (movable in Game.movables) {
        if (movable !is Tank) continue

        val tankType = TwTankType.getTankTypeFromName(movable.name) ?: continue

        tankType.onSharedUpdate?.invoke(movable)
    }
}

fun sharedDrawTanks() {
    val drawing = Drawing.drawing!!
    for (movable in Game.movables) {
        if (movable !is Tank) continue

        val twTankType = TwTankType.getTankTypeFromName(movable.name) ?: continue
        val buildingProps = twTankType.buildingProperties
        val (r, g, b) = getTeamColorOrGray(movable.team)

        if (buildingProps != null) {
            drawing.setColor(r, g, b)
            drawing.setFontSize(50.0)
            drawing.drawText(movable.posX, movable.posY-movable.size, buildingProps.displayName)
        }

        twTankType.onSharedDraw?.invoke(movable)
    }
}