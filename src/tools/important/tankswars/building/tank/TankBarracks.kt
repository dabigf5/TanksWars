package tools.important.tankswars.building.tank

import tanks.tank.Tank
import tools.important.tankswars.tank.TankSoldier

class TankBarracks(name: String, x: Double, y: Double, angle: Double) : TankBuilding(
    name,
    x,
    y,
    angle,
) {
    init {
        spawnedTankEntries.add(SpawnedTankEntry(TankSoldier("tw_soldier", 0.0, 0.0, 0.0), 1.0))

        spawnedMaxCount = 10
    }

    override fun capture(capturingTank: Tank?) {
        super.capture(capturingTank)

        for (attacker in spawnedTanks) {
            (attacker as TankSoldier).disown()
        }
    }
}