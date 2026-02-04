package tools.important.tankswars.core

import tanks.Game
import tanks.Team
import tanks.network.event.EventTankRemove
import tanks.tank.Explosion
import tanks.tank.Mine
import tanks.tank.Tank
import tools.important.tankswars.building.tank.TankBuilding
import tools.important.tankswars.building.tank.TankKeepBase
import tools.important.tankswars.util.broadcastFleeMessage

fun flee(team: Team, source: Tank? = null) {
    News.broadcastFleeMessage(team)

    for (movable in Game.movables) {
        if (movable.team != team) continue

        if (movable is TankBuilding) {
            if (movable.type.buildingProperties!!.captureProperties != null) {
                if (movable is TankKeepBase) movable.liability = false
                if (movable != source) movable.silentCapture(null)
                continue
            }
            movable.destroy = true
            Game.eventsOut.add(EventTankRemove(movable, true))
            continue
        }

        if (movable is Explosion) {
            movable.damage = 0.0
            continue
        }

        if (movable is Mine) {
            movable.explosion.damage = 0.0
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