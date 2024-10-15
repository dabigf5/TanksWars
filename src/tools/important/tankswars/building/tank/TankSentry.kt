package tools.important.tankswars.building.tank

import tanks.bullet.Bullet

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
        bullet.speed = 12.0
        bullet.size = 5.0
        bullet.damage = 0.04
        bullet.effect = Bullet.BulletEffect.fire
        bullet.maxLiveBullets = 0
        bullet.cooldownBase = 2.5

        enableDefensiveFiring = true
        enablePredictiveFiring = false

        turretIdleSpeed = 0.14
        turretAimSpeed = 0.04

        cooldownBase = 0.0
        cooldownRandom = 0.0

        emblem = "emblems/angry.png"
        emblemR = 255.0
        emblemG = 255.0
        emblemB = 255.0
    }
}