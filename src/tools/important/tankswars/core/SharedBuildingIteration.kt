package tools.important.tankswars.core

import tanks.Drawing
import tanks.Game
import tanks.tank.Tank
import tools.important.tankswars.building.BuildingType
import tools.important.tankswars.util.getTeamColorOrGray

fun sharedPreUpdateBuildings() {
    for (movable in Game.movables) {
        if (movable !is Tank) continue

        val buildingType = BuildingType.getBuildingTypeFromName(movable.name) ?: continue

        if (buildingType.stationary) {
            movable.vX = 0.0
            movable.vY = 0.0
            movable.orientation = 0.0

            movable.posX = movable.lastPosX
            movable.posY = movable.lastPosY
        }

        buildingType.onSharedPreUpdate?.invoke(movable)
    }
}

fun sharedUpdateBuildings() {
    for (movable in Game.movables) {
        if (movable !is Tank) continue

        val buildingType = BuildingType.getBuildingTypeFromName(movable.name) ?: continue

        buildingType.onSharedUpdate?.invoke(movable)
    }
}

fun sharedDrawBuildings() {
    val drawing = Drawing.drawing!!
    for (movable in Game.movables) {
        if (movable !is Tank) continue

        val buildingType = BuildingType.getBuildingTypeFromName(movable.name) ?: continue
        val (r, g, b) = getTeamColorOrGray(movable.team)

        drawing.setColor(r, g, b)
        drawing.drawText(movable.posX, movable.posY-movable.size, movable.posZ, buildingType.displayName)

        buildingType.onSharedDraw?.invoke(movable)
    }
}