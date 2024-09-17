package tools.important.tankswars.util

import io.netty.buffer.ByteBuf
import tanks.network.NetworkUtils

fun ByteBuf.writeString(str: String) = NetworkUtils.writeString(this, str)

fun ByteBuf.readString(): String = NetworkUtils.readString(this)