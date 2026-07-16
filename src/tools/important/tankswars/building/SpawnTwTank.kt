package tools.important.tankswars.building

import tanks.Game
import tanks.Team

fun spawnTwTank(building: TwTankType, x: Double, y: Double, angle: Double = 0.0, team: Team? = null) {
    val tank = building.tankClass.getConstructor(String::class.java, Double::class.java, Double::class.java, Double::class.java).newInstance(
        building.registryName, x, y, angle
    )
    tank.team = team
    Game.addTank(tank)
}