package tools.important.tankswars.event

import io.netty.buffer.ByteBuf
import tanks.gui.screen.ScreenPartyLobby
import tanks.network.event.PersonalEvent
import tools.important.tankswars.News
import tools.important.tankswars.NewsMessageType
import tools.important.tankswars.util.readString
import tools.important.tankswars.util.writeString

/**
 * An event used to send a news message, where both the message and the message type are fully defined.
 * This event, however, is not the only one that sends news messages. Others such as `EventBuildingWasCaptured` will infer
 * some information on the client.
 *
 * @see EventBuildingWasCaptured
 */
class EventNewsMessage(
    var message: String? = null,
    var messageType: NewsMessageType? = null
) : PersonalEvent() {

    override fun write(buf: ByteBuf) {
        buf.writeString(message!!)
        buf.writeInt(messageType!!.ordinal)
    }

    override fun read(buf: ByteBuf) {
        message = buf.readString()
        messageType = NewsMessageType.entries.getOrNull(buf.readInt()) ?: NewsMessageType.CAPTURE_NEUTRAL
    }

    override fun execute() {
        if (ScreenPartyLobby.isClient) News.sendMessage(message!!, messageType!!)
    }
}