package tools.important.tankswars.event.to_client

import io.netty.buffer.ByteBuf
import tanks.gui.screen.ScreenPartyLobby
import tanks.network.event.PersonalEvent
import tanks.tank.Tank
import tools.important.tankswars.core.CommandingSystem
import tools.important.tankswars.core.OrderMessage
import tools.important.tankswars.util.readString
import tools.important.tankswars.util.writeString

/**
 * This event is sent by the server whenever the host or a connected client
 * issues a command to their nearby soldier teammates.
 * @see tools.important.tankswars.core.CommandingSystem
 */
class EventCommandMessage(
    var message: OrderMessage? = null
) : PersonalEvent() {
    override fun write(buf: ByteBuf) {
        buf.writeString(message!!.text)
        buf.writeDouble(message!!.remainingTime)
        buf.writeInt(message!!.orderer.networkID)
        buf.writeInt(message!!.visualTarget?.networkID ?: EventBuildingWasSilentlyCaptured.NIL_ID)
    }

    override fun read(buf: ByteBuf) {
        val messageText = buf.readString()
        val time = buf.readDouble()
        val orderer = Tank.idMap[buf.readInt()]!!

        val visualTargetId = buf.readInt()
        val visualTarget = if(visualTargetId != EventBuildingWasSilentlyCaptured.NIL_ID) Tank.idMap[visualTargetId] else null

        message = OrderMessage(
            messageText,
            orderer,
            visualTarget,
            time
        )
    }

    override fun execute() {
        if (!ScreenPartyLobby.isClient) return
        CommandingSystem.recentOrders.add(message!!)
    }
}