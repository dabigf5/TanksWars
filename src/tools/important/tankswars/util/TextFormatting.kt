package tools.important.tankswars.util

import tanks.Team

const val RESET_COLOR = "§255255255255"

fun colorText(color: Triple<Double, Double, Double>, text: String): String {
    return "${colorFormat(color)}$text$RESET_COLOR"
}

fun teamColorText(team: Team?, text: String): String {
    return "${teamColorFormat(team)}$text$RESET_COLOR"
}

fun teamColorFormat(team: Team?): String {
    if (team == null) return "§128128128255"

    if (team.enableColor)
        return colorFormat(team.teamColorR.toInt(), team.teamColorG.toInt(), team.teamColorB.toInt())

    return RESET_COLOR
}

fun colorFormat(color: Triple<Number, Number, Number>): String {
    return colorFormat(color.first.toInt(), color.second.toInt(), color.third.toInt())
}

fun colorFormat(r: Int, g: Int, b: Int): String {
    return "§%03d%03d%03d255".format(r,g,b)
}

fun String.upperFirst(): String {
    return this.replaceFirstChar {if (it.isLowerCase()) it.uppercase() else it.toString()}
}

fun String.formatInternalName(): String {
    return replace('_', ' ').upperFirst()
}