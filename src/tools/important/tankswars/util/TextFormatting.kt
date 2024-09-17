package tools.important.tankswars.util

import tanks.Team

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


fun String.upperFirst(): String {
    return this.replaceFirstChar {if (it.isLowerCase()) it.uppercase() else it.toString()}
}

fun String.formatInternalName(): String {
    return replace('_', ' ').upperFirst()
}