package tools.important.tankswars.twtank.tank

import tanks.Drawing
import tanks.Game
import tanks.Panel
import tanks.Team
import tanks.bullet.BulletEffect
import tanks.network.event.EventTankUpdateHealth
import tanks.tank.Tank
import tools.important.tankswars.core.SharedSystem
import tools.important.tankswars.util.getTeamColorOrGray
import kotlin.math.min

class TankSentry(name: String, x: Double, y: Double, angle: Double) : TankBuilding(
    name,
    x,
    y,
    angle,
) {
    init {
        shootAIType = ShootAI.straight

        turretLength = 65.0
        turretSize = 10.0

        bullet.bounces = 0
        bullet.speed = 24.0
        bullet.size = 5.0
        bullet.damage = 0.04
        bullet.effect = BulletEffect.fire
        bullet.maxLiveBullets = 0
        bulletItem!!.item.cooldownBase = 5.0

        enablePredictiveFiring = false
        enableDefensiveFiring = true

        turretIdleSpeed = 0.04
        turretAimSpeed = 0.04

        cooldownBase = 0.0
        cooldownRandom = 0.0

        emblem = "emblems/angry.png"
    }
}

const val DISPENSER_RADIUS = Game.tile_size * 2.0

class TankDispenser(name: String, x: Double, y: Double, angle: Double) : TankBuilding(
    name,
    x,
    y,
    angle,
) {
    init {
        enablePredictiveFiring = false

        turretIdleSpeed = 0.04
        turretAimSpeed = 0.04

        cooldownBase = 0.0
        cooldownRandom = 0.0

        emblem = "emblems/medic.png"
    }

    private companion object {
        const val DISPENSER_MAX_METAL = 400
        const val DISPENSER_METAL_REGEN_TIME = 200.0
        const val DISPENSER_METAL_PER_TRANSFER = 40
        const val DISPENSER_METAL_PER_REGEN = 40
        // dispenser health regen builds up over time
        const val DISPENSER_TIME_TO_REACH_MAX_HEALTH_REGEN = 600.0
        const val DISPENSER_MAX_HEALTH_REGEN_PER_FRAME = 1.0/1000.0
    }

    // tank to how long it has been in range
    val inRange = mutableMapOf<Tank, Double>()

    // slightly longer time for the first regen
    var metalRegenCooldown = DISPENSER_METAL_REGEN_TIME * 1.5
    var metalGiveCooldown = 0.0

    fun attemptTransferMetal(engineer: TankSoldierEngineer) {
        val dispenserMetal = SharedSystem.getInt(this, "metal")
        val engineerMetal = SharedSystem.getInt(engineer, "metal")

        val metalGiven = min(min(DISPENSER_METAL_PER_TRANSFER, dispenserMetal), TankSoldierEngineer.ENGINEER_MAX_METAL - engineerMetal)
        if (metalGiven <= 0) return

        val dispenserMetalAfterTransfer = dispenserMetal - metalGiven
        val engineerMetalAfterTransfer = engineerMetal + metalGiven

        SharedSystem.broadcastSetProperty(
            this, "metal", dispenserMetalAfterTransfer
        )
        SharedSystem.broadcastSetProperty(
            engineer, "metal", engineerMetalAfterTransfer
        )

        metalGiveCooldown = DISPENSER_METAL_REGEN_TIME
    }

    fun attemptRegenMetal() {
        val dispenserMetal = SharedSystem.getInt(this, "metal")
        if (dispenserMetal == DISPENSER_MAX_METAL) return

        val dispenserMetalAfterRegen = min(DISPENSER_MAX_METAL, dispenserMetal + DISPENSER_METAL_PER_REGEN)
        SharedSystem.broadcastSetProperty(
            this, "metal", dispenserMetalAfterRegen
        )
        metalRegenCooldown = DISPENSER_METAL_REGEN_TIME
    }

    override fun update() {
        super.update()

        if (destroy) return

        SharedSystem.broadcastSetPropertyIfNull(this, "metal", 0)

        if (metalRegenCooldown > 0) {
            metalRegenCooldown -= Panel.frameFrequency
        } else {
            attemptRegenMetal()
        }

        for (movable in Game.movables) {
            if (movable !is Tank) continue
            if (movable is TankBuilding) continue // do not heal buildings
            if (!Team.isAllied(movable, this)) {
                inRange.remove(movable)
                continue
            }
            if (distanceBetween(movable, this) > DISPENSER_RADIUS) {
                inRange.remove(movable)
                continue
            }
            if (inRange.contains(movable)) {
                inRange[movable] = inRange[movable]!! + Panel.frameFrequency
            } else {
                inRange[movable] = 0.0
            }
            val newTimeInRange = inRange[movable]!!
            val buildup = min(1.0, newTimeInRange / DISPENSER_TIME_TO_REACH_MAX_HEALTH_REGEN)

            val healthRegened = DISPENSER_MAX_HEALTH_REGEN_PER_FRAME * buildup

            movable.health = min(movable.baseHealth, movable.health + healthRegened)
            Game.eventsOut.add(EventTankUpdateHealth(movable))

            if (movable is TankSoldierEngineer) {
                if (metalGiveCooldown > 0) {
                    metalGiveCooldown -= Panel.frameFrequency
                } else {
                    attemptTransferMetal(movable)
                }
            }
        }
    }
}

val dispenserSharedDraw = fun(tank: Tank) {
    if (tank.destroy) return
    drawMetalCount(tank)
    val drawing = Drawing.drawing

    drawing.setColor(getTeamColorOrGray(tank.team).also { it.alpha = 40.0 })
    drawing.fillOval(tank.posX, tank.posY, DISPENSER_RADIUS * 2, DISPENSER_RADIUS * 2)
}