package tools.important.tankswars.event.to_client

import io.netty.buffer.ByteBuf
import tanks.gui.screen.ScreenPartyLobby
import tanks.network.event.PersonalEvent
import tanks.tank.Tank
import tools.important.tankswars.twtank.TwTankType
import tools.important.tankswars.core.News
import tools.important.tankswars.core.SharedSystem
import tools.important.tankswars.util.sendCaptureMessage

/**
 * An event used to signal to clients that a building was captured.
 * This will cause the sending of a news message, the sound effect, and a change of team of the captured building.
 */
class EventBuildingWasCaptured(
    var capturedTankId: Int? = null,
    var capturingTankId: Int? = null
) : PersonalEvent() {
    override fun write(buf: ByteBuf) {
        buf.writeInt(capturedTankId!!)
        buf.writeInt(capturingTankId!!)
    }

    override fun read(buf: ByteBuf) {
        capturedTankId = buf.readInt()
        capturingTankId = buf.readInt()
    }

    override fun execute() {
        if (!ScreenPartyLobby.isClient) return
        val capturedTank = Tank.idMap[capturedTankId]
        val capturingTank = Tank.idMap[capturingTankId]

        News.sendCaptureMessage(capturedTank!!, capturingTank!!)

        val type = TwTankType.getTankTypeFromName(capturedTank.name)!!
        type.buildingProperties?.captureProperties?.onSharedCapture?.invoke(capturedTank)

        capturedTank.team = capturingTank.team

        SharedSystem.setProperty(capturedTank, "timeSinceCapture", 0.0)
    }
}