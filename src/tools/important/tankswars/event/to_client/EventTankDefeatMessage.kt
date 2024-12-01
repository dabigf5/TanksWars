package tools.important.tankswars.event.to_client

import io.netty.buffer.ByteBuf
import tanks.gui.screen.ScreenPartyLobby
import tanks.network.event.PersonalEvent
import tools.important.tankswars.core.News
import tools.important.tankswars.util.*

/**
 * An event that is sent by the server when a tank is defeated.
 */
class EventTankDefeatMessage(
    var name: String? = null,
    var colorR: Int? = null,
    var colorG: Int? = null,
    var colorB: Int? = null,
    var allied: Boolean? = null,
) : PersonalEvent() {
    override fun write(buf: ByteBuf) {
        buf.writeString(name!!)
        buf.writeInt(colorR!!)
        buf.writeInt(colorG!!)
        buf.writeInt(colorB!!)
        buf.writeBoolean(allied!!)
    }

    override fun read(buf: ByteBuf) {
        name = buf.readString()

        colorR = buf.readInt()
        colorG = buf.readInt()
        colorB = buf.readInt()

        allied = buf.readBoolean()
    }

    override fun execute() {
        if (!ScreenPartyLobby.isClient) return
        val name = name!!

        News.sendDefeatMessage(
            name,
            Color(
                colorR!!.toDouble(),
                colorG!!.toDouble(),
                colorB!!.toDouble(),
            ),
            allied!!
        )
    }
}