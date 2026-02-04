package tools.important.tankswars.building.tank

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
        // todo: figure this out
//        bullet.effect = Bullet.BulletEffect.fire
        bullet.maxLiveBullets = 0
        bullet.item.cooldown = 2.5

        enableDefensiveFiring = true
        enablePredictiveFiring = false

        turretIdleSpeed = 0.14
        turretAimSpeed = 0.04

        cooldownBase = 0.0
        cooldownRandom = 0.0

        emblem = "emblems/angry.png"
        emblemColor = basewindow.Color(255.0, 255.0, 255.0)
    }
}