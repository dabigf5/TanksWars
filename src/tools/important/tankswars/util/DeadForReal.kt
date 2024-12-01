package tools.important.tankswars.util

import tanks.tank.Tank

val Tank.isDeadForReal
    get() = health < 0.00000001