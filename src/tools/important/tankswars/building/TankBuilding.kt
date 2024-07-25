package tools.important.tankswars.building

import tanks.*
import tanks.tank.TankAIControlled
import tools.important.tankswars.News
import tools.important.tankswars.NewsMessageType
import tools.important.tankswars.teamColorText

const val unclaimedHealth = 0.001

interface TankBuildingSpawner {
    val defaultChance: Double
}

interface TankBuildingCapturable {
    fun onCapture(originalTeam: Team?, capturingTeam: Team?) {}
    fun onManualCapture(originalTeam: Team?, capturingTeam: Team?) {}
}

fun TankBuildingCapturable.capture(capturingTeam: Team?) {
    if (this !is TankBuilding) return

    onCapture(team, capturingTeam)

    team = capturingTeam
    health = if (capturingTeam != null) baseHealth else unclaimedHealth
    destroy = false
}

fun TankBuildingCapturable.manualCapture(capturingTeam: Team) {
    if (this !is TankBuilding) return

    News.sendMessage(
        "${teamColorText(team, buildingDisplayName)} was captured by ${teamColorText(capturingTeam, capturingTeam.name.upperFirst())}!",

        if (capturingTeam == Game.playerTank.team)
            NewsMessageType.CAPTURE_GOOD
        else
            if (team == Game.playerTank.team)
                NewsMessageType.CAPTURE_BAD
            else
                NewsMessageType.NEUTRAL_CAPTURE
    )

    onManualCapture(team, capturingTeam)
    capture(capturingTeam)
}

interface TankBuildingStationary

fun TankBuildingStationary.resetPosition() {
    if (this !is TankBuilding) return
    posX = startingPosX
    posY = startingPosY

    vX = 0.0
    vY = 0.0

    orientation = 0.0
}

abstract class TankBuilding(name: String, x: Double, y: Double, angle: Double) : TankAIControlled(
    name,
    x,
    y,
    50.0,
    100.0,
    100.0,
    100.0,
    angle,
    ShootAI.none
) {
    val startingPosX = x
    val startingPosY = y

    init {
        spawnedInitialCount = 0
        turretLength = 0.0

        enableMineLaying = false
        enableMovement = false
        enableTracks = false

        glowSize = 0.0
        glowIntensity = 0.0
    }

    abstract val buildingDisplayName: String


    override fun damage(amount: Double, source: IGameObject?): Boolean {
        if (source == null) {
            return false
        }

        if (source !is Movable) return false
        if (source.team == null) return false

        super.damage(amount, source)

        if (health > 0) {

            return false
        }

        if (this is TankBuildingCapturable) {
            manualCapture(source.team)
            return false
        }

        News.sendMessage(
            "${teamColorText(team, buildingDisplayName)} has been destroyed!",
            if (Team.isAllied(Game.playerTank, this)) NewsMessageType.BAD_THING_HAPPENED else NewsMessageType.GOOD_THING_HAPPENED
        )

        return true
    }

    override fun preUpdate() {
        if (this is TankBuildingStationary) resetPosition()

        super.preUpdate()
    }

    override fun update() {
        super.update()
        if (team == null) health = unclaimedHealth

        if (this is TankBuildingSpawner) spawnChance = if (team != null) defaultChance else 0.0
    }

    private fun drawTextOverhead(text: String, r: Double, g: Double, b: Double) {
        Drawing.drawing.setColor(r, g, b)
        Drawing.drawing.setFontSize(Drawing.drawing.titleSize * 1.5)

        val intX = Drawing.drawing.toInterfaceCoordsX(posX)
        val intY = Drawing.drawing.toInterfaceCoordsY(posY - size*1.2)

        Drawing.drawing.drawInterfaceText(intX, intY, text)
    }

    override fun draw() {
        val color = getColor()
        val r = color.first
        val g = color.second
        val b = color.third

        drawTextOverhead(buildingDisplayName, r,g,b)

        super.draw()
    }

    fun getColor(): Triple<Double, Double, Double> {
        val r = if (team != null) team.teamColorR else 128.0
        val g = if (team != null) team.teamColorG else 128.0
        val b = if (team != null) team.teamColorB else 128.0

        return Triple(r, g, b)
    }
}