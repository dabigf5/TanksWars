package tools.important.tankswars.twtank

import tanks.tank.TankAIControlled
import tools.important.tankswars.core.SharedSystem

/**
 * Base class for all tanks added by this extension, exists for easier death tracking.
 */
abstract class TwTank(
    name: String,
    x: Double,
    y: Double,
    size: Double,
    r: Double,
    g: Double,
    b: Double,
    angle: Double,
    ai: ShootAI
) : TankAIControlled(
    name,
    x,
    y,
    size,
    r,
    g,
    b,
    angle,
    ai
) {
    protected fun twOnDeath() {
        SharedSystem.broadcastClearProperties(this)
    }
}