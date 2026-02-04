package tools.important.tankswars.util

import basewindow.Color
import tanks.Team

operator fun Color.component1(): Double {
    return red
}
operator fun Color.component2(): Double {
    return green
}
operator fun Color.component3(): Double {
    return blue
}

fun getTeamColorOrGray(team: Team?): Color {
    if (team == null) return Color(128.0, 128.0, 128.0)

    if (!team.enableColor) return Color(255.0, 255.0, 255.0)

    return team.teamColor
}