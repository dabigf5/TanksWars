package tools.important.tankswars.util

import tanks.tank.Tank

// note: scrutinize this next time there's a tank death-related bug
val Tank.isDeadForReal
    get() = health < 0.00000001