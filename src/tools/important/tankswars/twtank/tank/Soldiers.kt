package tools.important.tankswars.twtank.tank

import basewindow.Color
import tanks.Drawing
import tanks.Game
import tanks.GameObject
import tanks.Movable
import tanks.Panel
import tanks.Team
import tanks.network.event.EventTankRemove
import tanks.network.event.EventTankUpdateHealth
import tanks.tank.Tank
import tools.important.tankswars.twtank.TwTankType
import tools.important.tankswars.twtank.spawnTwTank
import tools.important.tankswars.core.BattleMessage
import tools.important.tankswars.core.BattleMessageSystem
import tools.important.tankswars.core.SharedSystem
import tools.important.tankswars.twtank.TwTank
import tools.important.tankswars.util.getTeamColorOrGray
import tools.important.tankswars.util.isDeadForReal
import kotlin.collections.iterator
import kotlin.math.min


interface TankCommandable {
    fun setTarget(m: Movable?)
    fun getTarget(): Movable?
}

open class TankSoldier(name: String, x: Double, y: Double, angle: Double) : TwTank(
    name,
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

    override fun damage(amount: Double, source: GameObject?): Boolean {
        val died = super.damage(amount, source)
        if (died) twOnDeath()
        return died
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

class TankSoldierEngineer(name: String, x: Double, y: Double, angle: Double) : TankSoldier(name, x, y, angle) {
    init {
        color = Color(60.0, 60.0, 60.0)
        secondaryColor = Color(130.0, 90.0, 60.0)

        enablePathfinding = false
        seekChance = 0.0

        enableLookingAtTargetEnemy = true
        targetEnemySightBehavior = TargetEnemySightBehavior.keep_distance

        size = 50.0
        health = 1.0
    }

    companion object { // why does this exist? because enum entries are null for some reason on the top level
        const val ENGINEER_MAX_METAL = 200
        const val ENGINEER_CALLOUT_TIME = 150.0
        const val ENGINEER_REPAIR_COOLDOWN = 50.0
        const val ENGINEER_BUILD_COOLDOWN = 25.0
        const val ENGINEER_MAX_METAL_SPENT_REPAIR = 50
        const val ENGINEER_REPAIR_RADIUS = Game.tile_size * 2
        val engineerMetalCosts = mapOf(
            TwTankType.SENTRY to 130,
            TwTankType.DISPENSER to 100
        )
        val engineerBuildSuffixes = listOf(
            " coming right up!",
            " going up!",
        )
    }

    var repairCooldown: Double = 0.0
    var buildCooldown: Double = 0.0
    val built = mutableMapOf<TwTankType, TankBuilding>()

    fun tryBuild(buildable: TwTankType) {
        val metal = SharedSystem.getInt(this, "metal")

        val metalCost = engineerMetalCosts[buildable]!!
        val existing = built[buildable]

        if (existing == null && metal >= metalCost) {
            val building = spawnTwTank(buildable, posX, posY) {
                it.team = team
            } as TankBuilding
            SharedSystem.broadcastSetProperty(this, "metal", metal - metalCost)
            val callout = "${buildable.buildingProperties!!.displayName}${engineerBuildSuffixes.random()}"

            BattleMessageSystem.broadcastMessage(BattleMessage(callout, this, remainingTime = ENGINEER_CALLOUT_TIME))
            built[buildable] = building
            buildCooldown = ENGINEER_BUILD_COOLDOWN
        }
    }

    fun tryRepair(tank: TankBuilding) {
        if (!Team.isAllied(this, tank)) return
        if (distanceBetween(this, tank) > ENGINEER_REPAIR_RADIUS) return
        if (tank.health >= tank.baseHealth) return
        val currentMetal = SharedSystem.getInt(this, "metal")
        if (currentMetal <= 0) return

        val metalCost = min(ENGINEER_MAX_METAL_SPENT_REPAIR, currentMetal)

        val finalMetal = currentMetal - metalCost

        val amountHealed = (metalCost / ENGINEER_MAX_METAL_SPENT_REPAIR.toDouble()) * 0.25

        tank.health = min(tank.health + amountHealed, tank.baseHealth)
        Game.eventsOut.add(EventTankUpdateHealth(tank))
        BattleMessageSystem.broadcastMessage(BattleMessage("+$amountHealed", tank))
        SharedSystem.broadcastSetProperty(this, "metal", finalMetal)
        repairCooldown = ENGINEER_REPAIR_COOLDOWN
    }

    override fun update() {
        super.update()
        SharedSystem.broadcastSetPropertyIfNull(this, "metal", ENGINEER_MAX_METAL)

        // if the engineer has no dispenser and can't afford one, just die;
        // it will only be a liability to its team without a metal supply, as it lingers on with a crumbling nest that could be
        // replaced by another engineer
        if (built[TwTankType.DISPENSER] == null && SharedSystem.getInt(this, "metal") < engineerMetalCosts[TwTankType.DISPENSER]!!) {
            destroy = true
            Game.eventsOut.add(EventTankRemove(this, true))
        }

        if (destroy || isDeadForReal) {
            for (building in built) {
                building.value.destroy = true
                Game.eventsOut.add(EventTankRemove(building.value, true))
            }
        } else {
            val markedForRemoval = mutableSetOf<TwTankType>()
            for (building in built) {
                if (building.value.isDeadForReal) {
                    markedForRemoval.add(building.key)
                }
            }
            for (buildingType in markedForRemoval) {
                built.remove(buildingType)
            }
        }

        if (destroy) return

        if (buildCooldown > 0) {
            buildCooldown -= Panel.frameFrequency
        } else {
            tryBuild(TwTankType.DISPENSER)
            tryBuild(TwTankType.SENTRY)
        }

        if (repairCooldown > 0) {
            repairCooldown -= Panel.frameFrequency
        } else {
            for (m in Game.movables) {
                if (m !is TankBuilding) continue
                tryRepair(m)
                if (repairCooldown > 0) break
            }
        }
    }
}

val drawMetalCount = fun(tank: Tank) {
    val drawing = Drawing.drawing

    val metal = SharedSystem.getIntOrNull(tank, "metal") ?: return

    val teamColor = tank.team.teamColor

    drawing.setColor(teamColor.red, teamColor.green, teamColor.blue)
    drawing.setFontSize(25.0)
    drawing.drawText(tank.posX, tank.posY + tank.size, "$metal metal")
}

val engineerSharedDraw = fun(tank: Tank) {
    val metal = SharedSystem.getIntOrNull(tank, "metal") ?: return

    drawMetalCount(tank)
    val drawing = Drawing.drawing

    if (metal == 0) {
        drawing.setColor(getTeamColorOrGray(tank.team).also { it.alpha = 64.0 })
    } else {
        drawing.setColor(getTeamColorOrGray(tank.team).also { it.alpha = 128.0 })
    }

    drawing.drawOval(
        tank.posX,
        tank.posY,
        TankSoldierEngineer.ENGINEER_REPAIR_RADIUS * 2,
        TankSoldierEngineer.ENGINEER_REPAIR_RADIUS * 2
    )
}