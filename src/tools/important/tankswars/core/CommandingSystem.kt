package tools.important.tankswars.core

import org.lwjgl.glfw.GLFW
import tanks.Drawing
import tanks.Game
import tanks.Movable
import tanks.Team
import tanks.gui.screen.ScreenGame
import tanks.gui.screen.ScreenPartyLobby
import tanks.tank.Tank
import tools.important.tankswars.event.to_server.EventIssueCommand
import tools.important.tankswars.twtank.tank.TankCommandable
import kotlin.math.sqrt

enum class CommandType(
    val key: Int,
    val executeServer: (Tank, Double, Double) -> Unit,
) {
    ON_ME(GLFW.GLFW_KEY_X, { commander, _, _ ->
        CommandingSystem.commandAllNearbyServer(commander, commander)

        BattleMessageSystem.broadcastMessage(BattleMessage.command("On me!", commander, null))
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

        BattleMessageSystem.broadcastMessage(BattleMessage.command("Over there!", commander, target))
    }),
    FORGET_ORDERS(GLFW.GLFW_KEY_V, { commander, _, _ ->
        CommandingSystem.commandAllNearbyServer(commander, null)
        BattleMessageSystem.broadcastMessage(BattleMessage.command("Forget orders!", commander, null))
    }),
}

const val COMMANDING_RADIUS = Game.tile_size * 6.0

object CommandingSystem {
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
    }

    fun draw() {

    }
}