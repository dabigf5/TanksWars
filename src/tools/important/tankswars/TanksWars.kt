package tools.important.tankswars

import main.Tanks
import tanks.Game
import tanks.Game.registerTank
import tanks.extension.Extension
import tanks.gui.screen.Screen
import tanks.gui.screen.ScreenGame
import tanks.gui.screen.ScreenPartyLobby
import tanks.gui.screen.leveleditor.ScreenLevelEditorOverlay
import tanks.tank.Tank
import tools.important.tankswars.building.BuildingType
import tools.important.tankswars.core.*
import tools.important.tankswars.event.to_client.EventBuildingWasCaptured
import tools.important.tankswars.event.to_client.EventBuildingWasSilentlyCaptured
import tools.important.tankswars.event.to_client.EventNewsMessage
import tools.important.tankswars.event.to_client.EventTeamFled
import tools.important.tankswars.tank.TankFiller
import tools.important.tankswars.tank.TankSoldier
import tools.important.tankswars.tank.TankSoldierCaptain
import tools.important.tankswars.tank.TankSoldierDefender
import tanks.network.NetworkEventMap.register as registerNetworkEvent


private fun registerTank0W(tankClass: Class<out Tank>, name: String) = registerTank(tankClass, name, 0.0)


private var fillerIndex = 0
private fun registerFiller() {
    fillerIndex++
    registerTank0W(TankFiller::class.java, "tw_filler$fillerIndex")
}
private fun registerFiller(amount: Int) {
    for (i in 1..amount) {
        registerFiller()
    }
}

object TanksWars {
    const val VERSION = "Tanks Wars 0.1.2"
    val buildingProperties: MutableMap<Tank, MutableMap<String, Any>> = mutableMapOf()
}

var lastScreen: Screen? = null

class TanksWarsExtension : Extension("TanksWars") {
    override fun setUp() {
        println("Currently running ${TanksWars.VERSION}")

        registerNetworkEvent(EventNewsMessage::class.java)
        registerNetworkEvent(EventBuildingWasCaptured::class.java)
        registerNetworkEvent(EventBuildingWasSilentlyCaptured::class.java)
        registerNetworkEvent(EventTeamFled::class.java)

        registerFiller(3) // skip to page 2

        registerTank0W(TankSoldier::class.java, "tw_soldier")
        registerTank0W(TankSoldierCaptain::class.java, "tw_soldiercaptain")
        registerTank0W(TankSoldierDefender::class.java, "tw_soldierdefender")
        registerFiller(10-3) // skip to next row

        for (buildingType in BuildingType.entries) {
            registerTank0W(buildingType.tankClass, buildingType.registryName)
        }
    }

    override fun preUpdate() {
        sharedPreUpdateBuildings()
    }

    override fun draw() {
        // no way to make it draw under the pause menu, this has to be done
        val screen = Game.screen
        if (!(screen is ScreenGame && screen.paused || screen is ScreenLevelEditorOverlay)) {
            sharedDrawBuildings()
        }

        News.draw()
    }

    override fun update() {
        if (Game.screen != lastScreen) TanksWars.buildingProperties.clear()

        News.update()
        if (!ScreenPartyLobby.isClient) {
            deathCheck()
        }
        sharedUpdateBuildings()

        lastScreen = Game.screen
    }
}


fun main() {
    Tanks.launchWithExtensions(arrayOf("debug"), arrayOf(TanksWarsExtension()), null)
}