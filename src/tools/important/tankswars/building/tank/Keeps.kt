package tools.important.tankswars.building.tank

import tanks.*
import tools.important.tankswars.TankSoldierDefender
import tools.important.tankswars.util.fuck
import tools.important.tankswars.util.upperFirst

open class TankKeep(name: String, x: Double, y: Double, angle: Double) : TankBuilding(
    name,
    x,
    y,
    angle
), TankBuildingSpawner, TankBuildingCapturable, TankBuildingStationary {
    override val defaultChance: Double = 0.12
    override val buildingDisplayName: String = "Keep"
    init {
        description = "A fortified keep that will spawn defensive tanks"

        turretLength = 0.0

        baseHealth = 8.0
        health = baseHealth

        enableMovement = false
        enableMineLaying = false

        spawnedMaxCount = 10
        spawnedInitialCount = 0

        spawnedTankEntries.add(SpawnedTankEntry(TankSoldierDefender("tw_soldierdefender", 0.0, 0.0, 0.0), 1.0))

        emblem = "emblems/square.png"
        emblemR = 255.0
        emblemG = 255.0
        emblemB = 255.0
    }
    private var timeSinceCapture = Double.POSITIVE_INFINITY

    override fun onCapture(originalTeam: Team?, capturingTeam: Team?) {
        if (capturingTeam != null) timeSinceCapture = 0.0

        for (defender in spawnedTanks) {
            defender.destroy = true
        }
    }

    override fun update() {
        super.update()

        timeSinceCapture += Panel.frameFrequency
    }

    private val boxSize = Game.tile_size * 9

    override fun draw() {
        val color = getColor()

        val r = color.first
        val g = color.second
        val b = color.third

        Drawing.drawing.setColor(color.first, color.second, color.third)

        Drawing.drawing.setColor(r, g, b, 80.0)
        Drawing.drawing.fillRect(posX, posY, boxSize, boxSize)

        if (timeSinceCapture <= circleTime) {
            drawCaptureCircle(timeSinceCapture)
        }

        Drawing.drawing.setColor(r,g,b)

        super.draw()
    }

    private val circleTime = 120
    private fun drawCaptureCircle(time: Double) {
        if (team == null) return

        val progress = (time / circleTime)

        val circleSize = progress * boxSize

        Drawing.drawing.setColor(team.teamColorR, team.teamColorG, team.teamColorB, 255.0 - progress * 255.0)
        Drawing.drawing.fillOval(posX, posY, circleSize, circleSize)
    }
}

class TankKeepBase(name: String, x: Double, y: Double, angle: Double) : TankKeep(
    name,
    x,
    y,
    angle
) {
    private var startingTeam: Team? = null

    override fun update() {
        super.update()
        if (team != null && startingTeam == null) startingTeam = team
    }

    private var isLiability = true
    override val buildingDisplayName: String
        get() {
            if (startingTeam == null) return "Base"
            return "${startingTeam!!.name.upperFirst()} Base"
        }

    init {
        description = "An important keep that will cause its team to lose if it is captured"

        baseHealth = 16.0
        health = baseHealth

        spawnedMaxCount = 8

        emblem = "emblems/star.png"
        emblemR = 255.0
        emblemG = 255.0
        emblemB = 255.0
    }

    override fun onManualCapture(originalTeam: Team?, capturingTeam: Team?) {
        super.onManualCapture(originalTeam, capturingTeam)
        if (isLiability) {
            if (this.team != null) fuck(this.team)
        }

        isLiability = false
    }

    override fun onCapture(originalTeam: Team?, capturingTeam: Team?) {
        super.onCapture(originalTeam, capturingTeam)
        emblem = "emblems/square.png"
    }
}