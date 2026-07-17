package tools.important.tankswars.core

import tanks.Drawing
import tanks.Game
import tanks.gui.screen.IConditionalOverlayScreen
import tanks.gui.screen.ScreenEditorTank
import tanks.gui.screen.ScreenGame
import tanks.gui.screen.ScreenPartyHost
import tanks.gui.screen.ScreenPartyLobby
import tanks.gui.screen.leveleditor.ScreenLevelEditorOverlay
import tanks.tank.Tank
import tools.important.tankswars.twtank.TwTankType
import tools.important.tankswars.twtank.tank.TankBuilding
import tools.important.tankswars.event.to_client.EventUpdateSharedProperty
import tools.important.tankswars.lastScreen
import tools.important.tankswars.util.component1
import tools.important.tankswars.util.component2
import tools.important.tankswars.util.component3
import tools.important.tankswars.util.getTeamColorOrGray

object SharedSystem {
    // networkid to properties
    private val sharedProperties: MutableMap<Int, MutableMap<String, Any>> = mutableMapOf()

    fun sharedUpdate() {
        if (Game.screen != lastScreen) sharedProperties.clear()
        sharedUpdateTanks()

        News.update()
        if (!ScreenPartyLobby.isClient) {
            deathCheck()
        }
        CommandingSystem.update()
        BattleMessageSystem.update()
    }

    fun sharedPreUpdate() {
        sharedPreUpdateTanks()
    }

    fun sharedDraw() {
        // no way to make it draw under the pause menu, this has to be done
        val screen = Game.screen
        if (!(screen is ScreenGame && (screen.paused) ||
                    screen is ScreenLevelEditorOverlay ||
                    screen is IConditionalOverlayScreen ||
                    screen is ScreenEditorTank)
        ) {
            sharedDrawTanks()
            CommandingSystem.draw()
            BattleMessageSystem.draw()
        }

        News.draw()
    }


    private fun sharedUpdateTanks() {
        for (movable in Game.movables) {
            if (movable !is Tank) continue

            val tankType = TwTankType.getTankTypeFromName(movable.name) ?: continue

            tankType.onSharedUpdate?.invoke(movable)
        }
    }

    private fun sharedPreUpdateTanks() {
        for (movable in Game.movables) {
            if (movable !is Tank) continue

            val tankType = TwTankType.getTankTypeFromName(movable.name) ?: continue

            if (tankType.buildingProperties?.stationary == true) {
                movable.vX = 0.0
                movable.vY = 0.0
                movable.orientation = 0.0

                if (movable is TankBuilding) {
                    // only reach if on the server
                    movable.posX = movable.startPosX
                    movable.posY = movable.startPosY
                }
            }

            tankType.onSharedPreUpdate?.invoke(movable)
        }
    }


    fun sharedDrawTanks() {
        val drawing = Drawing.drawing!!
        for (movable in Game.movables) {
            if (movable !is Tank) continue

            val twTankType = TwTankType.getTankTypeFromName(movable.name) ?: continue
            val buildingProps = twTankType.buildingProperties
            val (r, g, b) = getTeamColorOrGray(movable.team)

            if (buildingProps != null) {
                drawing.setColor(r, g, b)
                drawing.setFontSize(50.0)
                drawing.drawText(movable.posX, movable.posY - movable.size, buildingProps.displayName)
            }

            twTankType.onSharedDraw?.invoke(movable)
        }
    }

    /**
     * A utility function to update a property of a building and inform all clients of this property update
     */
    fun broadcastSetProperty(tank: Tank, propertyName: String, value: Any?) {
        setProperty(tank, propertyName, value)
        if (ScreenPartyHost.isServer) {
            Game.eventsOut.add(EventUpdateSharedProperty(tank.networkID, propertyName, value?.javaClass, value))
        }
    }

    private fun initializeProperties(tankId: Int) {
        sharedProperties.putIfAbsent(tankId, mutableMapOf())
    }

    private fun initializeProperties(tank: Tank) {
        sharedProperties.putIfAbsent(tank.networkID, mutableMapOf())
    }

    fun setProperty(tankId: Int, propertyName: String, value: Any?) {
        initializeProperties(tankId)
        val props = sharedProperties[tankId]!!
        if (value == null) {
            props.remove(propertyName)
            return
        }
        props[propertyName] = value
    }

    fun setProperty(tank: Tank, propertyName: String, value: Any?) {
        initializeProperties(tank)
        val props = sharedProperties[tank.networkID]!!
        if (value == null) {
            props.remove(propertyName)
            return
        }
        props[propertyName] = value
    }

    fun setPropertyIfNull(tank: Tank, propertyName: String, value: Any) {
        if (getPropertyOrNull(tank, propertyName) == null) {
            setProperty(tank, propertyName, value)
        }
    }

    fun broadcastSetPropertyIfNull(tank: Tank, propertyName: String, value: Any) {
        if (getPropertyOrNull(tank, propertyName) == null) {
            broadcastSetProperty(tank, propertyName, value)
        }
    }

    fun <T> getPropertyOrDefault(tank: Tank, propertyName: String, init: T): T {
        @Suppress("UNCHECKED_CAST")
        return sharedProperties[tank.networkID]!![propertyName] as T? ?: init
    }

    fun getPropertyOrNull(tank: Tank, propertyName: String): Any? {
        initializeProperties(tank)
        return sharedProperties[tank.networkID]!![propertyName]
    }

    fun getProperty(tank: Tank, propertyName: String): Any {
        return getPropertyOrNull(tank, propertyName)!!
    }

    fun getInt(tank: Tank, propertyName: String): Int {
        return getProperty(tank, propertyName) as Int
    }

    fun getIntOrNull(tank: Tank, propertyName: String): Int? {
        return getPropertyOrNull(tank, propertyName) as Int?
    }

    fun getDouble(tank: Tank, propertyName: String): Double {
        return getProperty(tank, propertyName) as Double
    }

    fun getDoubleOrNull(tank: Tank, propertyName: String): Double? {
        return getPropertyOrNull(tank, propertyName) as Double?
    }
}