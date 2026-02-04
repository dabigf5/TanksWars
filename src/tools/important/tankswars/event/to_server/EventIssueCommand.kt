package tools.important.tankswars.event.to_server

import io.netty.buffer.ByteBuf
import tanks.gui.screen.ScreenPartyLobby
import tanks.network.event.PersonalEvent
import tanks.tank.Tank
import tanks.tank.TankPlayerRemote
import tools.important.tankswars.core.CommandType

/**
 * This event is sent by a connected client whenever they
 * issue a command to their nearby soldier teammates via the `CommandingSystem`.
 * @see tools.important.tankswars.core.CommandingSystem
 */
class EventIssueCommand(
    var commandType: CommandType? = null
) : PersonalEvent() {
    override fun write(buf: ByteBuf) {
        buf.writeInt(commandType!!.ordinal)
    }

    override fun read(buf: ByteBuf) {
        commandType = CommandType.entries[buf.readInt()]
    }

    override fun execute() {
        if (ScreenPartyLobby.isClient) return

        val commanderTank = Tank.idMap.entries.find {
            val plrTank = it.value as? TankPlayerRemote ?: return@find false
            plrTank.player.clientID == clientID
        } ?.value as? TankPlayerRemote

        if (commanderTank != null) {
            commandType!!.executeServer(commanderTank)
        }
    }
}