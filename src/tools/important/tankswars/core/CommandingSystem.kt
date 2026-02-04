package tools.important.tankswars.core

import org.lwjgl.glfw.GLFW
import tanks.Drawing
import tanks.Game
import tanks.Movable
import tanks.Panel
import tanks.Team
import tanks.gui.screen.ScreenGame
import tanks.gui.screen.ScreenPartyLobby
import tanks.tank.Tank
import tools.important.tankswars.event.to_client.EventCommandMessage
import tools.important.tankswars.event.to_server.EventIssueCommand
import tools.important.tankswars.tank.TankCommandable
import kotlin.math.sqrt

class OrderMessage (
    val text: String,
    val orderer: Tank,
    val visualTarget: Tank?,
    var remainingTime: Double = 50.0,
)

enum class CommandType(
    val key: Int,
    val executeServer: (Tank, Double, Double) -> Unit,
) {
    ON_ME(GLFW.GLFW_KEY_X, { commander, _, _ ->
        CommandingSystem.commandAllNearbyServer(commander, commander)

        OrderMessage("On me!", commander, null).also { msg ->
            CommandingSystem.recentOrders.add(msg)
            Game.eventsOut.add(EventCommandMessage(msg))
        }
    }),
    OVER_THERE(GLFW.GLFW_KEY_C, execute@{ commander, mX, mY ->
        val player = Game.playerTank

        var lastDistance = Double.POSITIVE_INFINITY
        var target: Tank? = null
        for (m in Game.movables) {
            if (m == player) continue
            if (m !is Tank) continue

            val dist = sqrt((mX - m.posX) * (mX - m.posX) + (m.posY - mY) * (m.posY - mY))

            if (dist < lastDistance) {
                lastDistance = dist
                target = m
            }
        }
        if (target == null) return@execute

        CommandingSystem.commandAllNearbyServer(commander, target)

        OrderMessage("Over there!", commander, target).also { msg ->
            CommandingSystem.recentOrders.add(msg)
            Game.eventsOut.add(EventCommandMessage(msg))
        }
    }),
    FORGET_ORDERS(GLFW.GLFW_KEY_V, { commander, _, _ ->
        CommandingSystem.commandAllNearbyServer(commander, null)
        OrderMessage("Forget orders!", commander, null).also { msg ->
            CommandingSystem.recentOrders.add(msg)
            Game.eventsOut.add(EventCommandMessage(msg))
        }
    }),
}

const val COMMANDING_RADIUS = Game.tile_size * 6.0

object CommandingSystem {
    val recentOrders = mutableListOf<OrderMessage>()

    fun commandAllNearbyServer(commander: Tank, target: Movable?) {
        for (m in Game.movables) {
            if (m !is TankCommandable) continue
            if (m == commander) continue
            if (!Team.isAllied(commander, m)) continue

            if (Movable.distanceBetween(commander, m) < COMMANDING_RADIUS) {
                m.setTarget(target)
            }
        }
    }

    fun update() {
        if (Game.screen !is ScreenGame) return

        for (commandType: CommandType in CommandType.entries) {
            if (Game.game.window.validPressedKeys.contains(commandType.key)) {
                Game.game.window.validPressedKeys.remove(commandType.key)
                if (ScreenPartyLobby.isClient) {
                    Game.eventsOut.add(EventIssueCommand(commandType, Drawing.drawing.mouseX, Drawing.drawing.mouseY))
                } else {
                    commandType.executeServer(Game.playerTank, Drawing.drawing.mouseX, Drawing.drawing.mouseY)
                }
            }
        }

        var i = 0
        while (i < recentOrders.size) {
            val order = recentOrders[i]
            order.remainingTime -= Panel.frameFrequency
            if (order.remainingTime <= 0) {
                recentOrders.removeAt(i)
                continue
            }
            i++
        }
    }

    fun draw() {
        for (order in recentOrders) {
            val transparencyMultiplier = order.remainingTime / 100.0
            val higherTransparencyMultiplier = transparencyMultiplier * 2.0

            val orderer = order.orderer
            val ordererTeam = order.orderer.team
            val target = order.visualTarget

            Drawing.drawing.setColor(ordererTeam.teamColor.red, ordererTeam.teamColor.green, ordererTeam.teamColor.blue, 255.0 * higherTransparencyMultiplier)
            Drawing.drawing.drawText(orderer.posX, orderer.posY + COMMANDING_RADIUS/2.0, order.text)
            Drawing.drawing.setColor(ordererTeam.teamColor.red, ordererTeam.teamColor.green, ordererTeam.teamColor.blue, 175.0 * transparencyMultiplier)
            Drawing.drawing.fillOval(orderer.posX, orderer.posY, COMMANDING_RADIUS * 2.0, COMMANDING_RADIUS * 2.0)

            if (target != null) {
                val targetTeam = target.team
                Drawing.drawing.setColor(targetTeam.teamColor.red, targetTeam.teamColor.green, targetTeam.teamColor.blue, 150.0 * transparencyMultiplier)
                Drawing.drawing.fillOval(target.posX, target.posY, COMMANDING_RADIUS/2.0, COMMANDING_RADIUS/2.0)
            }
        }
    }
}