package tools.important.tankswars.event.to_client

import io.netty.buffer.ByteBuf
import tanks.network.event.PersonalEvent
import tools.important.tankswars.core.SharedSystem
import tools.important.tankswars.util.readString
import tools.important.tankswars.util.writeString

fun writeArbitraryClass(buf: ByteBuf, any: Any?) {
    when (any) {
        null -> {
            buf.writeByte(0)
        }
        is Int -> {
            buf.writeInt(any)
        }
        is Double -> {
            buf.writeDouble(any)
        }
        is String -> {
            buf.writeString(any)
        }
        is Boolean -> {
            buf.writeBoolean(any)
        }
        else ->
            error("Unsupported class ${any.javaClass.name}")
    }
}
fun readArbitraryClass(buf: ByteBuf, className: String): Any? {
    when (className) {
        "" -> {
            buf.readByte() // skip the 0
            return null
        }
        "java.lang.Integer" -> {
            return buf.readInt()
        }
        "java.lang.Double" -> {
            return buf.readDouble()
        }
        "java.lang.String" -> {
            return buf.readString()
        }
        "java.lang.Boolean" -> {
            return buf.readBoolean()
        }
    }

    error("Unrecognized class $className")
}

/**
 * An event which tells clients to update a key in a tank's properties in their `TanksWars.buildingProperties`
 * Uses direct ID storage rather than tank storage because of a strange race condition where some tanks are unavailable on the first update
 */
class EventUpdateSharedProperty(
    var tankId: Int? = null,
    var propertyName: String? = null,
    var propertyType: Class<*>? = null,
    var propertyValue: Any? = null,
) : PersonalEvent() {
    override fun write(buf: ByteBuf) {
        buf.writeInt(tankId!!)
        buf.writeString(propertyName!!)
        buf.writeString(propertyType!!.name)
        writeArbitraryClass(buf, propertyValue)
    }

    override fun read(buf: ByteBuf) {
        tankId = buf.readInt()
        propertyName = buf.readString()
        propertyValue = readArbitraryClass(buf, buf.readString())
    }

    override fun execute() {
        SharedSystem.setProperty(tankId!!, propertyName!!, propertyValue)
    }
}