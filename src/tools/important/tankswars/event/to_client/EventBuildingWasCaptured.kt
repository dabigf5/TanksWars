package tools.important.tankswars.event.to_client

import io.netty.buffer.ByteBuf
import tanks.gui.screen.ScreenPartyLobby
import tanks.network.event.PersonalEvent
import tanks.tank.Tank
import tools.important.tankswars.core.News
import tools.important.tankswars.util.sendCaptureMessage

/**
 * An event used to signal to clients that a building was captured.
 * This will cause the sending of a news message, the sound effect, and a change of team of the captured building.
 */
class EventBuildingWasCaptured(
    var capturedTank: Tank? = null,
    var capturingTank: Tank? = null
) : PersonalEvent() {
    override fun write(buf: ByteBuf) {
        buf.writeInt(capturedTank!!.networkID)
        buf.writeInt(capturingTank!!.networkID)
    }

    override fun read(buf: ByteBuf) {
        val capturedId = buf.readInt()
        val capturingId = buf.readInt()

        capturedTank = Tank.idMap[capturedId]
        capturingTank = Tank.idMap[capturingId]
    }

    override fun execute() {
        if (!ScreenPartyLobby.isClient) return
        News.sendCaptureMessage(capturedTank!!, capturingTank!!)
        capturedTank!!.team = capturingTank!!.team
    }
}