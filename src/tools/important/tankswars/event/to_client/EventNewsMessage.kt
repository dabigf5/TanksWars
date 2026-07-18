package tools.important.tankswars.event.to_client

import io.netty.buffer.ByteBuf
import tanks.gui.screen.ScreenPartyLobby
import tanks.network.event.PersonalEvent
import tools.important.tankswars.core.News
import tools.important.tankswars.core.NewsMessageType
import tools.important.tankswars.util.readString
import tools.important.tankswars.util.writeString

/**
 * An event used to send a news message to clients, where both the message and the message type are fully defined.
 * This event, however, is not the only one that sends news messages. Others such as `EventBuildingWasCaptured` will
 * also send news messages, while inferring
 * some information on the client.
 *
 * @see EventBuildingWasCaptured
 */
class EventNewsMessage(
    var message: String? = null,
    var messageTypeOrdinal: Int? = null
) : PersonalEvent() {

    override fun write(buf: ByteBuf) {
        buf.writeString(message!!)
        buf.writeInt(messageTypeOrdinal!!)
    }

    override fun read(buf: ByteBuf) {
        message = buf.readString()
        messageTypeOrdinal = buf.readInt()
    }

    override fun execute() {
        val messageType = NewsMessageType.entries.getOrNull(messageTypeOrdinal!!) ?: NewsMessageType.CAPTURE_NEUTRAL
        if (ScreenPartyLobby.isClient) News.sendMessage(message!!, messageType)
    }
}