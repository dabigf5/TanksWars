package tools.important.tankswars.twtank.tank

import tanks.tank.Tank

class TankCollege(name: String, x: Double, y: Double, angle: Double) : TankBuilding(
    name,
    x,
    y,
    angle,
) {
    init {
        spawnedTankEntries.add(SpawnedTankEntry(TankSoldierEngineer("tw_soldierengi", 0.0, 0.0, 0.0), 1.0))
        spawnedInitialCount = 1
        spawnedMaxCount = 1

        emblem = "emblems/tophat.png"
    }

    override fun capture(capturingTank: Tank?) {
        super.capture(capturingTank)

        for (attacker in spawnedTanks) {
            (attacker as TankSoldierEngineer).disown()
        }
    }
}