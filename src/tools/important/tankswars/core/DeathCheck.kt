package tools.important.tankswars.core

import tanks.Game
import tanks.Team
import tanks.gui.screen.ScreenGame
import tanks.gui.screen.ScreenPartyHost
import tanks.tank.Tank
import tools.important.tankswars.event.to_client.EventTankDefeated
import tools.important.tankswars.util.getTeamColorOrGray
import tools.important.tankswars.util.sendDefeatMessage

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

        News.sendDefeatMessage(tank)

        // this cannot be done with the tank, as its destroy is set to true
        // which means it's probably already been removed on the client
        if (ScreenPartyHost.isServer) {
            for (connection in ScreenPartyHost.server.connections) {
                val (r, g, b) = getTeamColorOrGray(team)

                connection.events.add(EventTankDefeated(
                    tank.name,

                    r.toInt(), g.toInt(), b.toInt(),

                    Team.isAllied(tank, connection.player.tank)
                ))
            }
        }

        if (wasCommander) flee(team)
    }
}