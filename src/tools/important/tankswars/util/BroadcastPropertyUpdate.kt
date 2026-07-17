package tools.important.tankswars.util

import tanks.Game
import tanks.gui.screen.ScreenPartyHost
import tanks.tank.Tank
import tools.important.tankswars.TanksWars
import tools.important.tankswars.event.to_client.EventUpdateSharedProperty

/**
 * A utility function to update a property of a building and inform all clients of this property update
 */
fun broadcastPropertyUpdate(tank: Tank, propertyName: String, value: Any) {
    TanksWars.buildingProperties[tank]!![propertyName] = value
    if (ScreenPartyHost.isServer) {
        Game.eventsOut.add(EventUpdateSharedProperty(tank, propertyName, value.javaClass, value))
    }
}