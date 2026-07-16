package tools.important.tankswars.core

import tanks.Drawing
import tanks.Game
import tanks.Panel
import tanks.tank.Tank
import tools.important.tankswars.event.to_client.EventBattleMessage

class BattleMessage (
    val text: String,
    val speaker: Tank,
    val visualTarget: Tank? = null,
    var remainingTime: Double = 50.0,
    var speakerCircleRadius: Double = 0.0,
) {
    companion object {
        fun command(text: String, commander: Tank, target: Tank?): BattleMessage {
            return BattleMessage(text, commander, target, speakerCircleRadius = COMMANDING_RADIUS)
        }
    }
}

object BattleMessageSystem {
    val recentMessages = mutableListOf<BattleMessage>()

    fun broadcastMessage(message: BattleMessage) {
        recentMessages.add(message)
        Game.eventsOut.add(EventBattleMessage(message))
    }

    fun update() {
        var i = 0
        while (i < recentMessages.size) {
            val message = recentMessages[i]
            message.remainingTime -= Panel.frameFrequency
            if (message.remainingTime <= 0) {
                recentMessages.removeAt(i)
                continue
            }
            i++
        }
    }

    fun draw() {
        for (message in recentMessages) {
            val transparencyMultiplier = message.remainingTime / 100.0
            val higherTransparencyMultiplier = transparencyMultiplier * 2.0

            val orderer = message.speaker
            val ordererTeam = message.speaker.team
            val target = message.visualTarget

            Drawing.drawing.setColor(ordererTeam.teamColor.red, ordererTeam.teamColor.green, ordererTeam.teamColor.blue, 255.0 * higherTransparencyMultiplier)
            Drawing.drawing.drawText(orderer.posX, orderer.posY + COMMANDING_RADIUS/2.0, message.text)

            if (message.speakerCircleRadius > 0.0) {
                Drawing.drawing.setColor(ordererTeam.teamColor.red, ordererTeam.teamColor.green, ordererTeam.teamColor.blue, 175.0 * transparencyMultiplier)
                Drawing.drawing.fillOval(orderer.posX, orderer.posY, message.speakerCircleRadius * 2.0, message.speakerCircleRadius * 2.0)
//                Drawing.drawing.fillOval(orderer.posX, orderer.posY, COMMANDING_RADIUS * 2.0, COMMANDING_RADIUS * 2.0)
            }

            if (target != null) {
                val targetTeam = target.team
                Drawing.drawing.setColor(targetTeam.teamColor.red, targetTeam.teamColor.green, targetTeam.teamColor.blue, 150.0 * transparencyMultiplier)
                Drawing.drawing.fillOval(target.posX, target.posY, COMMANDING_RADIUS/2.0, COMMANDING_RADIUS/2.0)
            }
        }
    }
}