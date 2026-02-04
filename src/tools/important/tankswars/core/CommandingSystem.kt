package tools.important.tankswars.core

import org.lwjgl.glfw.GLFW
import tanks.Drawing
import tanks.Game
import tanks.Movable
import tanks.Panel
import tanks.Team
import tanks.gui.screen.ScreenGame
import tanks.tank.Tank
import tools.important.tankswars.tank.TankCommandable
import kotlin.math.sqrt

class OrderMessage (
    val message: String,
    val orderer: Movable,
    val target: Movable?,
    var remainingTime: Double = 50.0,
)

const val COMMANDING_RADIUS = Game.tile_size * 6.0

object CommandingSystem {
    val recentOrders = mutableListOf<OrderMessage>()

    fun commandAllNearby(target: Movable?) {
        val player = Game.playerTank

        for (m in Game.movables) {
            if (m == player) continue
            if (!Team.isAllied(player, m)) continue
            if (m !is TankCommandable) {
                continue
            }
            if (Movable.distanceBetween(player, m) < COMMANDING_RADIUS) {
                m.setTarget(target)
            }
        }
    }

    fun overThere() {
        val mX = Drawing.drawing.mouseX
        val mY = Drawing.drawing.mouseY

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

        if (target == null) return

        commandAllNearby(target)
        recentOrders.add(OrderMessage("Over there!", Game.playerTank, target))
        Drawing.drawing.playSound("join.ogg", 1f, 0.1f)
    }

    fun onMe() {
        commandAllNearby(Game.playerTank)
        recentOrders.add(OrderMessage("On me!", Game.playerTank, null))
    }

    fun forgetOrders() {
        commandAllNearby(null)
        recentOrders.add(OrderMessage("Forget orders!", Game.playerTank, null))
    }

    fun update() {
        if (Game.screen !is ScreenGame) return

        if (Game.game.window.validPressedKeys.contains(GLFW.GLFW_KEY_C)) {
            Game.game.window.validPressedKeys.remove(GLFW.GLFW_KEY_C)
            overThere()
        } else if (Game.game.window.validPressedKeys.contains(GLFW.GLFW_KEY_X)) {
            Game.game.window.validPressedKeys.remove(GLFW.GLFW_KEY_X)
            onMe()
        } else if (Game.game.window.validPressedKeys.contains(GLFW.GLFW_KEY_V)) {
            Game.game.window.validPressedKeys.remove(GLFW.GLFW_KEY_V)
            forgetOrders()
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
            val target = order.target

            Drawing.drawing.setColor(ordererTeam.teamColor.red, ordererTeam.teamColor.green, ordererTeam.teamColor.blue, 255.0 * higherTransparencyMultiplier)
            Drawing.drawing.drawText(orderer.posX, orderer.posY + COMMANDING_RADIUS/2.0, order.message)
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