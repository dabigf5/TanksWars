package tools.important.tankswars.event.to_client

import io.netty.buffer.ByteBuf
import tanks.gui.screen.ScreenPartyLobby
import tanks.network.event.PersonalEvent
import tanks.tank.Tank
import tools.important.tankswars.TanksWars
import tools.important.tankswars.building.tank.TankKeepBase

/**
 * An event used to signal to clients that a building was captured.
 * Unlike `EventBuildingWasCaptured` this will only cause a change of team of the captured building, and will not
 * show anything in the news.
 *
 * This event is used only when a lot of buildings are being captured at once, such as when a liable `TankKeepBase` is captured.
 *
 * @see TankKeepBase
 * @see EventBuildingWasCaptured
 */
class EventBuildingWasSilentlyCaptured(
    var capturedTank: Tank? = null,
    var capturingTank: Tank? = null
) : PersonalEvent() {
    override fun write(buf: ByteBuf) {
        buf.writeInt(capturedTank!!.networkID)
        buf.writeInt(capturingTank?.networkID ?: -1)
    }

    override fun read(buf: ByteBuf) {
        val capturedId = buf.readInt()
        val capturingId = buf.readInt()

        capturedTank = Tank.idMap[capturedId]
        if (capturingId != -1) capturingTank = Tank.idMap[capturingId]
    }

    override fun execute() {
        if (!ScreenPartyLobby.isClient) return

        capturedTank!!.team = capturingTank?.team

        TanksWars.buildingProperties.putIfAbsent(capturedTank!!, mutableMapOf())
        TanksWars.buildingProperties[capturedTank]!!["timeSinceCapture"] = 0.0
    }
}