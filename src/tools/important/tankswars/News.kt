package tools.important.tankswars

import tanks.Drawing
import tanks.Panel
import tanks.Team

private const val startingLifetime = 300.0
private class NewsMessage(
    val text: String,
) {
    var lifetime: Double = startingLifetime
}

enum class NewsMessageType(
    val soundName: String,
    val soundPitch: Float,
) {
    GOOD_THING_HAPPENED("join.ogg", 1.5f),
    BAD_THING_HAPPENED("leave.ogg", 1.0f),
    CAPTURE_GOOD("bonus1.ogg", 1.5f),
    CAPTURE_BAD("bonus1.ogg", 0.5f),
    NEUTRAL_CAPTURE("rampage.ogg",1f)
}

fun teamColorText(team: Team?, text: String): String {
    return "${teamColorFormat(team)}$text§255255255255"
}

fun teamColorFormat(team: Team?): String {
    if (team == null) return "§128128128255"

    if (team.enableColor)
        return colorToStringFormat(team.teamColorR.toInt(), team.teamColorG.toInt(), team.teamColorB.toInt())

    return "§255255255255"
}

fun colorToStringFormat(r: Int, g: Int, b: Int): String {
   return "§%03d%03d%03d255".format(r,g,b)
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
            val opacity = ((message.lifetime / startingLifetime) * 127.5) + 72.5

            val text = message.text

            Drawing.drawing.setColor(0.0, 0.0, 0.0, opacity)
            Drawing.drawing.fillInterfaceRect(newsX, newsY - offset, 900.0, 100.0)

            Drawing.drawing.setColor(255.0, 255.0, 255.0, 255.0)

            Drawing.drawing.drawInterfaceText(newsX, newsY - offset, text)
        }
    }
}