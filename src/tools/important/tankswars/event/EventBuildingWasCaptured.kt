package tools.important.tankswars.event

import io.netty.buffer.ByteBuf
import tanks.Game
import tanks.Team
import tanks.network.event.PersonalEvent
import tanks.tank.Tank
import tools.important.tankswars.News
import tools.important.tankswars.NewsMessageType
import tools.important.tankswars.building.BuildingType
import tools.important.tankswars.util.teamColorText

/**
 * An event used to signal that a building was captured.
 * This will cause the sending of a news message, as well as the sound effect.
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
        val buildingType = BuildingType.getBuildingTypeFromName(capturedTank!!)!!
        News.sendMessage(
            "${teamColorText(capturedTank!!.team, buildingType.displayName)} was captured by ${teamColorText(capturingTank!!.team, capturingTank!!.name)}",
            if (Team.isAllied(Game.playerTank, capturedTank))
                NewsMessageType.CAPTURE_BAD
            else
                if (Team.isAllied(Game.playerTank, capturingTank))
                    NewsMessageType.CAPTURE_GOOD
                else
                    NewsMessageType.CAPTURE_NEUTRAL
        )

        capturedTank!!.team = capturingTank!!.team
    }
}