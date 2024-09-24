package tools.important.tankswars.core

import tanks.Game
import tanks.Team
import tanks.network.event.EventTankRemove
import tanks.tank.Explosion
import tanks.tank.Mine
import tanks.tank.Tank
import tools.important.tankswars.building.tank.TankBuilding
import tools.important.tankswars.building.tank.TankKeepBase
import tools.important.tankswars.event.to_client.EventTeamFled
import tools.important.tankswars.util.getTeamColorOrGray
import tools.important.tankswars.util.sendFleeMessage

fun flee(team: Team, source: Tank? = null) {
    val color = getTeamColorOrGray(team)

    News.sendFleeMessage(team.name, color)

    Game.eventsOut.add(EventTeamFled(team.name, color))

    for (movable in Game.movables) {
        if (movable.team != team) continue

        if (movable is TankBuilding) {
            if (movable.type.captureProperties != null) {
                if (movable is TankKeepBase) movable.liability = false
                if (movable != source) movable.silentCapture(null)
            }

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

        if (movable is Tank) {
            movable.destroy = true
            Game.eventsOut.add(EventTankRemove(movable, true))
            continue
        }

        movable.destroy = true
    }
}