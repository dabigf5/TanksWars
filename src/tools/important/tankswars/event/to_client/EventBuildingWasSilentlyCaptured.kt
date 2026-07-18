package tools.important.tankswars.event.to_client

import io.netty.buffer.ByteBuf
import tanks.gui.screen.ScreenPartyLobby
import tanks.network.event.PersonalEvent
import tanks.tank.Tank
import tools.important.tankswars.twtank.tank.TankKeepBase
import tools.important.tankswars.core.SharedSystem
import tools.important.tankswars.event.NIL_ID

/**
 * An event used to signal to clients that a building was captured.
 * Unlike `EventBuildingWasCaptured`, this will only cause a change of team of the captured building, and will not
 * show anything in the news.
 *
 * This event is used only when a lot of buildings are being captured at once, such as when a liable `TankKeepBase` is captured.
 *
 * @see TankKeepBase
 * @see EventBuildingWasCaptured
 */
class EventBuildingWasSilentlyCaptured(
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

        val capturingTank = if (capturingTankId != NIL_ID) Tank.idMap[capturingTankId] else null
        val capturedTank = Tank.idMap[capturedTankId]
        capturedTank!!.team = capturingTank?.team

        SharedSystem.setProperty(capturedTank, "timeSinceCapture", 0.0)
    }

}