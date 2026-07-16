package tools.important.tankswars.event.to_client

import io.netty.buffer.ByteBuf
import tanks.gui.screen.ScreenPartyLobby
import tanks.network.event.PersonalEvent
import tanks.tank.Tank
import tools.important.tankswars.core.BattleMessage
import tools.important.tankswars.core.BattleMessageSystem
import tools.important.tankswars.event.NIL_ID
import tools.important.tankswars.util.readString
import tools.important.tankswars.util.writeString

/**
 * This event is sent by the server whenever the host or a connected client
 * issues a command to their nearby soldier teammates, or whenever a tank issues a callout
 * (such as an engineer announcing that it is building a sentry).
 * @see tools.important.tankswars.core.CommandingSystem
 * @see tools.important.tankswars.tank.TankSoldierEngineer
 */
class EventBattleMessage(
    var message: BattleMessage? = null
) : PersonalEvent() {
    override fun write(buf: ByteBuf) {
        buf.writeString(message!!.text)
        buf.writeDouble(message!!.remainingTime)
        buf.writeInt(message!!.speaker.networkID)
        buf.writeInt(message!!.visualTarget?.networkID ?: NIL_ID)
        buf.writeDouble(message!!.speakerCircleRadius)
    }

    override fun read(buf: ByteBuf) {
        val messageText = buf.readString()
        val remainingTime = buf.readDouble()
        val speaker = Tank.idMap[buf.readInt()]!!

        val visualTargetId = buf.readInt()
        val visualTarget = if(visualTargetId != NIL_ID) Tank.idMap[visualTargetId] else null

        val speakerCircleRadius = buf.readDouble()

        message = BattleMessage(
            messageText,
            speaker,
            visualTarget,
            remainingTime,
            speakerCircleRadius
        )
    }

    override fun execute() {
        if (!ScreenPartyLobby.isClient) return
        BattleMessageSystem.recentMessages.add(message!!)
    }
}