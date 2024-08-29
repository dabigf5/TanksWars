package tools.important.tankswars

import tanks.Drawing
import tanks.Panel

private class NewsMessage(
    val text: String,
) {
    companion object {
        const val STARTING_NEWS_MESSAGE_LIFETIME = 300.0
    }

    var lifetime: Double = STARTING_NEWS_MESSAGE_LIFETIME
}

enum class NewsMessageType(
    val soundName: String,
    val soundPitch: Float,
) {
    GOOD_THING_HAPPENED("join.ogg", 1.5f),
    BAD_THING_HAPPENED("leave.ogg", 1.0f),
    CAPTURE_GOOD("bonus1.ogg", 1.5f),
    CAPTURE_BAD("bonus1.ogg", 0.5f),
    CAPTURE_NEUTRAL("rampage.ogg",1f)
}

object News {
    private val newsMessages: MutableList<NewsMessage> = mutableListOf()

    fun sendMessage(text: String, type: NewsMessageType) {
        newsMessages.add(NewsMessage(text))
        Drawing.drawing.playSound(type.soundName, type.soundPitch)
    }

    fun update() {
        var i = 0
        while (i < newsMessages.size) {
            val message = newsMessages[i]

            message.lifetime -= Panel.frameFrequency
            if (message.lifetime > 0) {
                i++
                continue
            }

            newsMessages.removeAt(i)
        }
    }

    fun draw() {
        val newsX = Drawing.drawing.interfaceSizeX * 0.5
        val newsY = Drawing.drawing.interfaceSizeY * 0.8

        val fontSize = Drawing.drawing.titleSize

        Drawing.drawing.setInterfaceFontSize(fontSize)
        for ((i, message) in newsMessages.withIndex()) {
            val offset = (Drawing.drawing.interfaceSizeY / 10) * (i+1)
            val opacity = ((message.lifetime / NewsMessage.STARTING_NEWS_MESSAGE_LIFETIME) * 127.5) + 72.5

            val text = message.text

            Drawing.drawing.setColor(0.0, 0.0, 0.0, opacity)
            Drawing.drawing.fillInterfaceRect(newsX, newsY - offset, 900.0, 100.0)

            Drawing.drawing.setColor(255.0, 255.0, 255.0, 255.0)

            Drawing.drawing.drawInterfaceText(newsX, newsY - offset, text)
        }
    }
}