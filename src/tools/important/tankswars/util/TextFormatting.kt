package tools.important.tankswars.util

import tanks.Team

const val RESET_COLOR = "§255255255255"

fun getColorEscape(color: Color): String {
    return getColorEscape(color.r, color.g, color.b)
}
fun getColorEscape(r: Double, g: Double, b: Double): String {
    return getColorEscape(r.toInt(), g.toInt(), b.toInt())
}
fun getColorEscape(r: Int, g: Int, b: Int): String {
    return "§%03d%03d%03d255".format(r,g,b)
}

fun coloredText(color: Color, text: String): String {
    return "${getColorEscape(color)}$text$RESET_COLOR"
}
fun teamColoredText(team: Team?, text: String): String {
    return coloredText(getTeamColorOrGray(team), text)
}

fun String.upperFirst(): String {
    return this.replaceFirstChar {if (it.isLowerCase()) it.uppercase() else it.toString()}
}

fun String.formatInternalName(): String {
    return replace('_', ' ').upperFirst()
}