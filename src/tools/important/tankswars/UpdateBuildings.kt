package tools.important.tankswars

import tanks.Drawing
import tanks.Game
import tanks.tank.Tank
import tools.important.tankswars.building.BuildingType
import tools.important.tankswars.building.tank.TankBuilding
import tools.important.tankswars.util.teamColorToBuildingColor

fun updateBuildings() {
    for (movable in Game.movables) {
        if (movable !is TankBuilding) continue

        val buildingType = BuildingType.getBuildingTypeFromClass(movable.javaClass) ?: continue

        if (buildingType.stationary) {
            movable.orientation = 0.0
            movable.posX = movable.lastPosX
            movable.posY = movable.lastPosY
        }
    }
}

fun drawBuildings() {
    val drawing = Drawing.drawing!!
    for (movable in Game.movables) {
        if (movable !is Tank) continue

        // the tank may be a TankRemote, which is why we do this instead of using the class one
        val buildingType = BuildingType.getBuildingTypeFromName(movable) ?: continue
        val color = teamColorToBuildingColor(movable.team)

        drawing.setColor(color.first, color.second, color.third)
        drawing.drawText(movable.posX, movable.posY-movable.size, movable.posZ, buildingType.displayName)

        buildingType.onDraw?.invoke(movable)
    }
}