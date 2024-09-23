package tools.important.tankswars.core

import tanks.Drawing
import tanks.Game
import tanks.gui.screen.ScreenPartyLobby
import tanks.tank.Tank
import tools.important.tankswars.building.BuildingType
import tools.important.tankswars.util.teamColorToBuildingColor

fun updateBuildings() {
    for (movable in Game.movables) {
        if (movable !is Tank) continue

        val buildingType = BuildingType.getBuildingTypeFromName(movable.name) ?: continue

        if (!ScreenPartyLobby.isClient) {
            if (buildingType.stationary) {
                movable.orientation = 0.0
                movable.posX = movable.lastPosX
                movable.posY = movable.lastPosY
            }
        }

        buildingType.onUpdate?.invoke(movable)
    }
}

fun drawBuildings() {
    val drawing = Drawing.drawing!!
    for (movable in Game.movables) {
        if (movable !is Tank) continue

        val buildingType = BuildingType.getBuildingTypeFromName(movable.name) ?: continue
        val (r, g, b) = teamColorToBuildingColor(movable.team)

        drawing.setColor(r, g, b)
        drawing.drawText(movable.posX, movable.posY-movable.size, movable.posZ, buildingType.displayName)

        buildingType.onDraw?.invoke(movable)
    }
}