package tools.important.tankswars.tank

import basewindow.Color
import tanks.Drawing
import tanks.Movable
import tanks.tank.Tank
import tanks.tank.TankAIControlled
import tools.important.tankswars.TanksWars
import tools.important.tankswars.building.TwTankType
import tools.important.tankswars.building.spawnTwTank
import tools.important.tankswars.building.tank.TankBuilding
import tools.important.tankswars.core.BattleMessage
import tools.important.tankswars.core.BattleMessageSystem
import tools.important.tankswars.util.broadcastPropertyUpdate


interface TankCommandable {
    fun setTarget(m: Movable?)
    fun getTarget(): Movable?
}

open class TankSoldier(name: String, x: Double, y: Double, angle: Double) : TankAIControlled(name,
    x,
    y,
    30.0,
    125.0,
    125.0,
    125.0,
    angle,
    ShootAI.straight
), TankCommandable {
    val tankType = TwTankType.getTankTypeFromClass(this::class.java)!!
    var orderedTarget: Movable? = null

    init {
        description = tankType.description

        enableMineLaying = false
        enableTracks = false

        enableMineAvoidance = true
        enableBulletAvoidance = true

        enablePredictiveFiring = false

        enablePathfinding = true
        stopSeekingOnSight = false
        seekChance = 1.0

        cooldownBase = 1000.0

        bullet.bounces = 0
        bullet.damage = 1.0 / 5.0

        health = 0.4

        glowSize = 0.0
        glowIntensity = 0.0
    }

    fun disown() {
        parent = null
    }

    override fun update() {
        super.update()
        if (team == null) destroy = true
    }

    override fun isInterestingPathTarget(m: Movable?): Boolean {
        if (orderedTarget != null) return m == orderedTarget
        return super.isInterestingPathTarget(m)
    }

    override fun setTarget(m: Movable?) {
        if (path != null) path.clear()
        orderedTarget = m
    }

    override fun getTarget(): Movable? {
        return orderedTarget
    }
}

class TankSoldierCaptain(name: String, x: Double, y: Double, angle: Double) : TankSoldier(name, x, y, angle) {
    init {
        color = Color(80.0, 80.0, 80.0)

        enableMineAvoidance = false
        enableBulletAvoidance = false
        cooldownBase = 500.0
        size = 50.0
        bullet.damage = 1.0 / 4.0

        health = 0.8
    }
}

class TankSoldierDefender(name: String, x: Double, y: Double, angle: Double) : TankSoldier(name, x, y, angle) {
    init {
        color = Color(60.0, 60.0, 60.0)

        enablePathfinding = false
        seekChance = 0.0

        stayNearParent = true
        maxDistanceFromParent = 100.0

        health = 0.6
    }
}

const val engineerCalloutTime = 150.0
class TankSoldierEngineer(name: String, x: Double, y: Double, angle: Double) : TankSoldier(name, x, y, angle) {
    init {
        color = Color(60.0, 60.0, 60.0)
        secondaryColor = Color(130.0, 90.0, 60.0)

        enablePathfinding = false
        seekChance = 0.0

        size = 50.0
        health = 1.0
    }

    private companion object { // why does this exist? because enum entries are null for some reason on the top level
        const val ENGINEER_MAX_METAL = 200
        val engineerMetalCosts = mapOf(
            TwTankType.SENTRY to 130
        )
        val engineerBuildSuffixes = listOf(
            " coming right up!",
            " going up!",
        )
    }

    val built = mutableMapOf<TwTankType, TankBuilding>()

    fun tryBuild(buildable: TwTankType) {
        val properties = TanksWars.buildingProperties[this]!!
        if (properties["metal"] == null) {
            properties["metal"] = ENGINEER_MAX_METAL
        }

        val metal = properties["metal"] as Int

        val metalCost = engineerMetalCosts[buildable]!!
        val existing = built[buildable]

        if (existing == null && metal >= metalCost) {
            spawnTwTank(buildable, posX, posY, team = team)
            broadcastPropertyUpdate(this, "metal", metal - metalCost)
            val callout = "${buildable.buildingProperties!!.displayName}${engineerBuildSuffixes.random()}"

            BattleMessageSystem.broadcastMessage(BattleMessage(callout, this, remainingTime = engineerCalloutTime))
        }
    }

    override fun update() {
        tryBuild(TwTankType.SENTRY)

        super.update()
    }
}
val engineerSharedDraw = fun(tank: Tank) {
    val properties = TanksWars.buildingProperties[tank]!!
    val drawing = Drawing.drawing

    val metal = properties["metal"] as? Int ?: return

    val teamColor = tank.team.teamColor

    drawing.setColor(teamColor.red, teamColor.green, teamColor.blue)
    drawing.setFontSize(25.0)
    drawing.drawText(tank.posX, tank.posY-tank.size, "$metal metal")
}