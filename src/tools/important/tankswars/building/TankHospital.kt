package tools.important.tankswars.building

import tanks.*
import tanks.tank.Tank
import kotlin.math.PI
import kotlin.math.min

class TankHospital(name: String, x: Double, y: Double, angle: Double) : TankBuilding(
    name,
    x,
    y,
    angle
), TankBuildingCapturable, TankBuildingStationary {
    override val buildingDisplayName: String = "Hospital"

    init {
        description = "Hospital with automated medical systems that will heal nearby allied tanks"
        emblem = "emblems/medic.png"
        emblemG = 200.0

        turretLength = 40.0
        bullet.shotCount = 4
        bullet.shotSpread = 360.0

        multipleTurrets = true

        baseHealth = 4.0
        health = baseHealth
    }

    private val healingRange = Game.tile_size * 2.0
    private val healingPerUpdate = 1.0 / 1000.0

    private var spin = 0.0
    override fun update() {
        super.update()

        spin += min(0.01 * Panel.frameFrequency, (2) * PI)
        angle = spin

        for (movable in Game.movables) {
            if (movable == this) continue
            if (movable !is Tank) continue
            if (!Team.isAllied(this, movable)) continue
            if (Movable.distanceBetween(this, movable) > healingRange) continue

            if (movable.health < movable.baseHealth) {
                movable.health = min(
                    movable.health + (healingPerUpdate * Panel.frameFrequency),
                    movable.baseHealth
                )
                if (movable == Game.playerTank) Drawing.drawing.playSound("heal.ogg", 1.0f, 0.1f)
            }
        }
    }

    override fun draw() {
        val color = getColor()

        val r = color.first
        val g = color.second
        val b = color.third

        Drawing.drawing.setColor(r,g,b,100.0)
        Drawing.drawing.fillOval(posX,posY, healingRange*2, healingRange*2)

        super.draw()
    }
}