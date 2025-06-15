package tools.important.tankswars.event.to_client

import io.netty.buffer.ByteBuf
import tanks.gui.screen.ScreenPartyLobby
import tanks.network.event.PersonalEvent
import tanks.tank.Tank
import tools.important.tankswars.util.*

/**
 * An event that is sent by the server to update a tank's emblem on the client.
 */
class EventTankEmblemUpdate(
    var tank: Tank? = null,
    var emblem: String? = null,
    var emblemR: Double? = null,
    var emblemG: Double? = null,
    var emblemB: Double? = null
) : PersonalEvent() {
    override fun write(buf: ByteBuf) {
        buf.writeInt(tank!!.networkID)
        buf.writeString(emblem!!)
        buf.writeDouble(emblemR!!)
        buf.writeDouble(emblemG!!)
        buf.writeDouble(emblemB!!)
    }

    override fun read(buf: ByteBuf) {
        tank = Tank.idMap[buf.readInt()]
        emblem = buf.readString()
        emblemR = buf.readDouble()
        emblemG = buf.readDouble()
        emblemB = buf.readDouble()
    }

    override fun execute() {
        if (!ScreenPartyLobby.isClient) return
        tank!!.emblem = emblem
    }
}