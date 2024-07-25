package tools.important.tankswars.building

import tanks.Team
import tools.important.tankswars.TankSoldier
import tools.important.tankswars.TankSoldierCaptain

open class TankBarracks(name: String, x: Double, y: Double, angle: Double) : TankBuilding(
    name,
    x,
    y,
    angle
), TankBuildingSpawner, TankBuildingCapturable, TankBuildingStationary {
    override val buildingDisplayName: String = "Barracks"

    init {
        description = "Barracks that will train and spawn offensive soldiers"

        emblem = "emblems/circle_outline.png"
        emblemR = 255.0
        emblemG = 255.0
        emblemB = 255.0

        turretLength = 0.0

        baseHealth = 3.0
        health = baseHealth


        enableMovement = false
        enableMineLaying = false

        spawnedMaxCount = 6
        spawnedInitialCount = 0
        spawnedTankEntries.add(SpawnedTankEntry(TankSoldierCaptain("tw_soldiercaptain", 0.0, 0.0, 0.0), 1.0))
        spawnedTankEntries.add(SpawnedTankEntry(TankSoldier("tw_soldier", 0.0, 0.0, 0.0), 30.0))
    }

    override fun onCapture(originalTeam: Team?, capturingTeam: Team?) {
        while (spawnedTanks.size > 0) {
            (spawnedTanks.removeAt(0) as TankSoldier).disown()
        }
    }

    override val defaultChance: Double = 0.03
}