package tools.important.tankswars.core

import tanks.Game.registerTank
import tanks.tank.Tank
import tools.important.tankswars.building.TwTankType
import tools.important.tankswars.event.to_client.*
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

fun initializeTanksWars() {
    registerNetworkEvent(EventNewsMessage::class.java)
    registerNetworkEvent(EventBuildingWasCaptured::class.java)
    registerNetworkEvent(EventBuildingWasSilentlyCaptured::class.java)
    registerNetworkEvent(EventTeamFled::class.java)
    registerNetworkEvent(EventTankDefeatMessage::class.java)
    registerNetworkEvent(EventBuildingWasDestroyed::class.java)
    registerNetworkEvent(EventTankEmblemUpdate::class.java)

    registerFiller(3) // skip to page 2

    registerTank0W(TankSoldier::class.java, "tw_soldier")
    registerTank0W(TankSoldierCaptain::class.java, "tw_soldiercaptain")
    registerTank0W(TankSoldierDefender::class.java, "tw_soldierdefender")
    registerFiller(10 - 3) // skip to next row

    for (buildingType in TwTankType.entries) {
        registerTank0W(buildingType.tankClass, buildingType.registryName)
    }
    registerFiller(TwTankType.entries.size)
}