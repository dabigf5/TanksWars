package tools.important.tankswars.tank

import basewindow.Color
import tanks.Movable
import tanks.tank.TankAIControlled
import tools.important.tankswars.building.TwTankType


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