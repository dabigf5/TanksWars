package tools.important.tankswars.util

import tanks.Team

typealias Color = Triple<Double, Double, Double>

val Color.r
    get() = first

val Color.g
    get() = second

val Color.b
    get() = third


val Team.teamColor
    get() = Color(teamColorR, teamColorG, teamColorB)



fun teamColorToBuildingColor(team: Team?): Color {
    val r = team?.teamColorR ?: 128.0
    val g = team?.teamColorG ?: 128.0
    val b = team?.teamColorB ?: 128.0

    return Triple(r, g, b)
}