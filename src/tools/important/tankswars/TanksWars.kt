package tools.important.tankswars

import main.Tanks
import tanks.Game
import tanks.extension.Extension
import tanks.tank.Tank
import tools.important.tankswars.util.deathCheck


private fun registerTank(tankClass: Class<out Tank>, name: String) {
    Game.registerTank(tankClass, name, 0.0)
}

class TanksWars : Extension("TanksWars") {
    companion object {
        @Suppress("unused")
        const val EXTENSION_VERSION = "Tanks Wars 0.1.2"
    }

    override fun setUp() {
        registerTank(TankSoldier::class.java, "tw_soldier")
        registerTank(TankSoldierCaptain::class.java, "tw_soldiercaptain")
        registerTank(TankSoldierDefender::class.java, "tw_soldierdefender")
    }

    override fun draw() {
        News.draw()
    }

    override fun update() {
        News.update()

        deathCheck()
    }
}


fun main() {
    Tanks.launchWithExtensions(arrayOf("debug"), arrayOf(TanksWars()), IntArray(0))
}