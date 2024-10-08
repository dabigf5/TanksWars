package tools.important.tankswars.core

import tanks.Game
import tanks.Team
import tanks.gui.screen.ScreenGame
import tanks.tank.Tank
import tanks.tank.TankPlayer
import tools.important.tankswars.building.tank.TankBuilding
import tools.important.tankswars.tank.TankSoldier
import tools.important.tankswars.util.formatInternalName
import tools.important.tankswars.util.teamColoredText

fun deathCheck() {
    if (ScreenGame.finished) return

    val screen = Game.screen!!
    if (screen is ScreenGame && screen.paused) return

    for (movable in Game.movables) {
        val team = movable.team ?: continue

        if (movable !is Tank) continue
        if (movable is TankSoldier) continue
        if (movable is TankBuilding) continue

        if (!movable.destroy) continue
        if (movable.destroyTimer > 0.0) continue

        val wasCommander = movable is TankPlayer || movable.name.startsWith("cmd")

        val movableNameFormatted = teamColoredText(team, movable.name.formatInternalName())

        News.sendMessage(
            "$movableNameFormatted has been defeated!",

            if (Team.isAllied(movable, Game.playerTank))
                NewsMessageType.BAD_THING_HAPPENED
            else
                NewsMessageType.GOOD_THING_HAPPENED
        )

        if (wasCommander) flee(team)
    }
}