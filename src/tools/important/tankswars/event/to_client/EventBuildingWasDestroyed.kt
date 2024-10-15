package tools.important.tankswars.event.to_client

import io.netty.buffer.ByteBuf
import tanks.gui.screen.ScreenPartyLobby
import tanks.network.event.PersonalEvent
import tools.important.tankswars.core.News
import tools.important.tankswars.util.*

/**
 * An event that is sent by the server when a building is destroyed.
 */
class EventBuildingWasDestroyed(
    var name: String? = null,

    var colorR: Int? = null,
    var colorG: Int? = null,
    var colorB: Int? = null,

    var destroyerTeamName: String? = null,

    var destroyerColorR: Int? = null,
    var destroyerColorG: Int? = null,
    var destroyerColorB: Int? = null,

    var allied: Boolean? = null,
) : PersonalEvent() {
    override fun write(buf: ByteBuf) {
        buf.writeString(name!!)

        buf.writeInt(colorR!!)
        buf.writeInt(colorG!!)
        buf.writeInt(colorB!!)

        buf.writeString(destroyerTeamName!!)

        buf.writeInt(destroyerColorR!!)
        buf.writeInt(destroyerColorG!!)
        buf.writeInt(destroyerColorB!!)

        buf.writeBoolean(allied!!)
    }

    override fun read(buf: ByteBuf) {
        name = buf.readString()

        colorR = buf.readInt()
        colorG = buf.readInt()
        colorB = buf.readInt()

        destroyerTeamName = buf.readString()

        destroyerColorR = buf.readInt()
        destroyerColorG = buf.readInt()
        destroyerColorB = buf.readInt()

        allied = buf.readBoolean()
    }

    override fun execute() {
        if (!ScreenPartyLobby.isClient) return
        val name = name!!

        News.sendDestroyMessage(
            Color(
                colorR!!.toDouble(),
                colorG!!.toDouble(),
                colorB!!.toDouble(),
            ),
            name,
            destroyerTeamName!!,
            Color(
                destroyerColorR!!.toDouble(),
                destroyerColorG!!.toDouble(),
                destroyerColorB!!.toDouble(),
            ),
            allied!!
        )
    }
}