package tools.important.tankswars.util

import tanks.Game
import tanks.Team
import tanks.tank.Explosion
import tanks.tank.Mine
import tools.important.tankswars.News
import tools.important.tankswars.NewsMessageType
import tools.important.tankswars.building.tank.TankBuildingCapturable
import tools.important.tankswars.building.tank.capture

fun fuck(team: Team) {
    News.sendMessage(
        "${teamColorText(team, team.name.upperFirst())} fled the battlefield!",
        if (team == Game.playerTank.team) NewsMessageType.BAD_THING_HAPPENED else NewsMessageType.GOOD_THING_HAPPENED
    )

    for (movable in Game.movables) {
        if (movable.team != team) continue

        if (movable is TankBuildingCapturable) {
            movable.capture(null)
            continue
        }

        if (movable is Explosion) {
            movable.damage = 0.0
            continue
        }

        if (movable is Mine) {
            movable.damage = 0.0
            movable.destroy = true
            continue
        }

        movable.destroy = true
    }
}