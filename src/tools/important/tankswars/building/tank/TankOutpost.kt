package tools.important.tankswars.building.tank

import tanks.Game
import tanks.network.event.EventTankRemove
import tanks.tank.Tank
import tools.important.tankswars.tank.TankSoldierDefender

class TankOutpost(name: String, x: Double, y: Double, angle: Double) : TankBuilding(
    name,
    x,
    y,
    angle,
) {
    init {
        spawnedTankEntries.add(SpawnedTankEntry(TankSoldierDefender("tw_soldierdefender", 0.0, 0.0, 0.0), 1.0))

        spawnedMaxCount = 6
    }

    override fun capture(capturingTank: Tank?) {
        super.capture(capturingTank)

        for (attacker in spawnedTanks) {
            attacker.destroy = true
            Game.eventsOut.add(EventTankRemove(attacker, true))
        }
    }
}