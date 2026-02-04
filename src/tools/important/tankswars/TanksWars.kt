package tools.important.tankswars

import main.Tanks
import tanks.Game
import tanks.extension.Extension
import tanks.gui.screen.*
import tanks.gui.screen.leveleditor.ScreenLevelEditorOverlay
import tanks.tank.Tank
import tools.important.tankswars.core.*

object TanksWars {
    const val VERSION = "Tanks Wars 0.2.3"
    val buildingProperties: MutableMap<Tank, MutableMap<String, Any>> = mutableMapOf()
}

var lastScreen: Screen? = null

class TanksWarsExtension : Extension("TanksWars") {
    override fun setUp() {
        println("Currently running ${TanksWars.VERSION}")
        initializeTanksWars()
    }

    override fun preUpdate() {
        sharedPreUpdateTanks()
    }

    override fun draw() {
        // no way to make it draw under the pause menu, this has to be done
        val screen = Game.screen
        if (!(
            screen is ScreenGame && (screen.paused) ||
            screen is ScreenLevelEditorOverlay ||
            screen is IConditionalOverlayScreen ||
            screen is ILevelPreviewScreen ||
            screen is ScreenEditorTank
        )) {
            sharedDrawTanks()
            CommandingSystem.draw()
        }

        News.draw()
    }

    override fun update() {
        if (Game.screen != lastScreen) TanksWars.buildingProperties.clear()

        News.update()
        if (!ScreenPartyLobby.isClient) {
            deathCheck()
        }
        sharedUpdateTanks()
        CommandingSystem.update()

        lastScreen = Game.screen
    }
}


fun main() {
    Tanks.launchWithExtensions(arrayOf("debug"), arrayOf(TanksWarsExtension()), null)
}