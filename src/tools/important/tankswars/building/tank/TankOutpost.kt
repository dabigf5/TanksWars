package tools.important.tankswars.building.tank

import tanks.Team
import tools.important.tankswars.TankSoldierDefender

class TankOutpost(name: String, x: Double, y: Double, angle: Double) : TankBuilding(
    name,
    x,
    y,
    angle
), TankBuildingSpawner, TankBuildingCapturable, TankBuildingStationary {
    override val buildingDisplayName: String = "Outpost"
    override val defaultChance: Double = 0.3

    init {
        description = "An outpost which will spawn defensive tanks"

        spawnedMaxCount = 8

        baseHealth = 5.0
        health = baseHealth

        spawnedTankEntries.add(SpawnedTankEntry(TankSoldierDefender("tw_soldierdefender", 0.0, 0.0, 0.0), 1.0))
    }

    override fun onCapture(originalTeam: Team?, capturingTeam: Team?) {
        for (defender in spawnedTanks) {
            defender.destroy = true
        }
    }
}