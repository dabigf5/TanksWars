package tools.important.tankswars.core

import tanks.Game
import tanks.gui.screen.ScreenGame
import tanks.tank.Tank
import tools.important.tankswars.util.broadcastDefeatMessage

fun shouldNotifyDeath(tank: Tank): Boolean {
    return !tank.name.startsWith("tw_")
}

fun deathCheck() {
    if (ScreenGame.finished) return

    val screen = Game.screen!!
    if (screen is ScreenGame && screen.paused) return

    for (tank in Game.movables) {
        val team = tank.team ?: continue
        if (tank !is Tank) continue
        if (!shouldNotifyDeath(tank)) continue

        if (!tank.destroy) continue
        if (tank.destroyTimer > 0.0) continue

        val wasCommander = tank.name.startsWith("cmd")

        News.broadcastDefeatMessage(tank)

        // this cannot be done with the tank, as its destroy is set to true
        // which means it's probably already been removed on the client

        if (wasCommander) flee(team)
    }
}