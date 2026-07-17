package tools.important.tankswars.building

import tanks.Game
import tanks.tank.Tank

fun spawnTwTank(building: TwTankType, x: Double, y: Double, angle: Double = 0.0, initializer: ((Tank) -> Unit)? = null): Tank {
    val tank = building.tankClass.getConstructor(String::class.java, Double::class.java, Double::class.java, Double::class.java).newInstance(
        building.registryName, x, y, angle
    )
    initializer?.invoke(tank)
    Game.addTank(tank)
    return tank
}