package tools.important.tankswars

import main.Tanks
import tanks.Game
import tanks.extension.Extension
import tanks.tank.Tank
import tools.important.tankswars.building.tank.*
import tools.important.tankswars.util.deathCheck


private fun register(tankClass: Class<out Tank>, name: String) {
    Game.registerTank(tankClass, name, 0.0)
}

class TanksWars : Extension("TanksWars") {
    companion object {
        @Suppress("unused")
        const val EXTENSION_VERSION = "Tanks Wars 0.1.2"
    }

    override fun setUp() {
        register(TankFiller::class.java, "tw_filler1")
        register(TankFiller::class.java, "tw_filler2")
        register(TankFiller::class.java, "tw_filler3")

        register(TankOutpost::class.java, "tw_outpost")
        register(TankKeep::class.java, "tw_keep")
        register(TankKeepBase::class.java, "tw_keepbase")
        register(TankBarracks::class.java, "tw_barracks")
        register(TankHospital::class.java, "tw_hospital")
        register(TankSentry::class.java, "tw_sentry")

        register(TankFiller::class.java, "tw_filler4")

        register(TankSoldier::class.java, "tw_soldier")
        register(TankSoldierCaptain::class.java, "tw_soldiercaptain")
        register(TankSoldierDefender::class.java, "tw_soldierdefender")
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