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

fun getTeamColorOrGray(team: Team?): Color {
    if (team == null) return Color(128.0, 128.0, 128.0)

    if (!team.enableColor) return Color(255.0, 255.0, 255.0)

    return team.teamColor
}