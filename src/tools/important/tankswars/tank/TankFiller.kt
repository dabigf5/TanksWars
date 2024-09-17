package tools.important.tankswars.tank

import tanks.tank.TankAIControlled

class TankFiller(name: String, x: Double, y: Double, angle: Double) : TankAIControlled(name,
    x,
    y,
    0.0,
    0.0,
    0.0,
    0.0,
    angle,
    ShootAI.none
)