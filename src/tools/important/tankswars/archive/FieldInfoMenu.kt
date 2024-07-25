package tools.important.tankswars.archive
//
//import org.lwjgl.glfw.GLFW
//import tanks.Drawing
//import tanks.Game
//import tanks.Team
//import tanks.tank.Tank
//import tools.important.tankswars.building.TankBuilding
//import tools.important.tankswars.building.upperFirst
//
//object FieldInfoMenu {
//    private var open: Boolean = false
//
//    fun update() {
//        open = (Game.game.window.pressedKeys.contains(GLFW.GLFW_KEY_TAB))
//        if (!open) return
//    }
//    fun draw() {
//        if (!open) return
//
//        val drawing = Drawing.drawing!!
//
//        drawing.setColor(0.0, 0.0, 0.0, 200.0)
//        drawing.fillInterfaceRect(
//            drawing.interfaceSizeX * 0.5,
//            drawing.interfaceSizeY * 0.3,
//            drawing.interfaceSizeX * 1.0,
//            drawing.interfaceSizeY * 0.4
//        )
//
//        val teamBuildingMap: MutableMap<Team, MutableList<TankBuilding>> = mutableMapOf()
//        val teamGeneralMap: MutableMap<Team, MutableList<Tank>> = mutableMapOf()
//
//        for (movable in Game.movables) {
//            if (movable.destroy) continue
//
//            if (movable is TankBuilding) {
//                teamBuildingMap.getOrPut(movable.team) { mutableListOf() }.add(movable)
//                continue
//            }
//            if (movable is TankSoldier) continue
//
//            if (movable is Tank) {
//                teamGeneralMap.getOrPut(movable.team) { mutableListOf() } .add(movable)
//            }
//        }
//
//        drawing.setColor(255.0,255.0,255.0)
//        drawing.drawInterfaceText(drawing.interfaceSizeX / 2, drawing.interfaceSizeY * 0.15, "Buildings")
//        for ((teamIndex, team) in teamBuildingMap.toSortedMap(compareBy {it.name}).keys.withIndex()) {
//            val x = 325.0 * (teamIndex+1) - 175.0
//            val y = drawing.interfaceSizeY * 0.2
//
//            drawing.setColor(team.teamColorR, team.teamColorG, team.teamColorB, 255.0)
//            drawing.setInterfaceFontSize(drawing.titleSize * 1.4)
//            drawing.drawInterfaceText(x, y, team.name.upperFirst())
//
//            val buildings = teamBuildingMap[team]!!
//            drawing.setInterfaceFontSize(drawing.titleSize * 0.6)
//            for ((buildingIndex, building) in buildings.withIndex()) {
//                val buildingY = y + (15.0 * (buildingIndex+1)) + 20.0
//                drawing.drawInterfaceText(x, buildingY, building.buildingDisplayName)
//            }
//        }
//
//        drawing.setColor(255.0, 255.0, 255.0)
//        drawing.drawInterfaceText(drawing.interfaceSizeX / 2, drawing.interfaceSizeY * 0.7, "Generals")
//    }
//}