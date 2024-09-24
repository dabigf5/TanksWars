package tools.important.tankswars.event.to_client

import io.netty.buffer.ByteBuf
import tanks.gui.screen.ScreenPartyLobby
import tanks.network.event.PersonalEvent
import tools.important.tankswars.core.News
import tools.important.tankswars.util.readString
import tools.important.tankswars.util.sendFleeMessage
import tools.important.tankswars.util.writeString

/**
 * An event that will be sent when a team flees the battlefield.
 *
 * This event only handles the news message being sent. (can't be done with `EventNewsMessage` due to the variable sound effect).
 *
 * This event will not handle the buildings' silent captures; that is handled separately via sending a bunch of `EventBuildingWasSilentlyCaptured`s.
 *
 * @see EventNewsMessage
 * @see EventBuildingWasSilentlyCaptured
 */
class EventTeamFled(
    var teamName: String? = null,
    var teamColor: Triple<Double, Double, Double>? = null
) : PersonalEvent() {
    override fun write(buf: ByteBuf) {
        buf.writeString(teamName!!)
        val (r, g, b) = teamColor!!

        buf.writeDouble(r)
        buf.writeDouble(g)
        buf.writeDouble(b)
    }

    override fun read(buf: ByteBuf) {
        teamName = buf.readString()

        val r = buf.readDouble()
        val g = buf.readDouble()
        val b = buf.readDouble()

        teamColor = Triple(r, g, b)
    }

    override fun execute() {
        if (!ScreenPartyLobby.isClient) return

        News.sendFleeMessage(teamName!!, teamColor!!)
    }
}