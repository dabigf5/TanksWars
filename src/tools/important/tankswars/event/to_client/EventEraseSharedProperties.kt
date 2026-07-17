package tools.important.tankswars.event.to_client

import io.netty.buffer.ByteBuf
import tanks.network.event.PersonalEvent
import tools.important.tankswars.core.SharedSystem

class EventEraseSharedProperties(
    var tankId: Int? = null,
) : PersonalEvent() {
    override fun write(buf: ByteBuf) {
        buf.writeInt(tankId!!)
    }

    override fun read(buf: ByteBuf) {
        tankId = buf.readInt()
    }

    override fun execute() {
        SharedSystem.clearProperties(tankId!!)
    }
}