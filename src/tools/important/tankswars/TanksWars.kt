package tools.important.tankswars

import main.Tanks
import tanks.Game
import tanks.extension.Extension
import tanks.gui.screen.Screen
import tools.important.tankswars.core.SharedSystem
import tools.important.tankswars.core.initializeTanksWars

object TanksWars {
    const val VERSION = "Tanks Wars 0.3.0"
}

var lastScreen: Screen? = null

class TanksWarsExtension : Extension("TanksWars") {
    override fun setUp() {
        println("Currently running ${TanksWars.VERSION}")
        initializeTanksWars()
    }

    override fun preUpdate() {
        SharedSystem.sharedPreUpdate()
    }

    override fun draw() {
        SharedSystem.sharedDraw()
    }

    override fun update() {
        SharedSystem.sharedUpdate()

        lastScreen = Game.screen
    }
}


fun main() {
    Tanks.launchWithExtensions(arrayOf("debug"), arrayOf(TanksWarsExtension()), null)
}