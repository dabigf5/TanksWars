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
 * @see tools.important.tankswars.twtank.tank.TankSoldierEngineer
 */
class EventBattleMessage(
    var text: String? = null,
    var time: Double? = null,
    var speakerId: Int? = null,
    var targetId: Int? = null,
    var speakerCircleRadius: Double? = null
) : PersonalEvent() {
    companion object {
        fun fromMessage(message: BattleMessage): EventBattleMessage {
            return EventBattleMessage(
                message.text,
                message.remainingTime,
                message.speaker.networkID,
                message.visualTarget?.networkID ?: NIL_ID,
                message.speakerCircleRadius
            )
        }
    }
    fun toMessage(): BattleMessage {
        return BattleMessage(
            text!!,
            Tank.idMap[speakerId!!]!!,
            Tank.idMap[targetId!!],
            time!!,
            speakerCircleRadius!!
        )
    }

    override fun write(buf: ByteBuf) {
        buf.writeString(text!!)
        buf.writeDouble(time!!)
        buf.writeInt(speakerId!!)
        buf.writeInt(targetId!!)
        buf.writeDouble(speakerCircleRadius!!)
    }

    override fun read(buf: ByteBuf) {
        text = buf.readString()
        time = buf.readDouble()
        speakerId = buf.readInt()
        targetId = buf.readInt()
        speakerCircleRadius = buf.readDouble()
    }

    override fun execute() {
        if (!ScreenPartyLobby.isClient) return
        BattleMessageSystem.recentMessages.add(toMessage())
    }
}