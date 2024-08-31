package tools.important.tankswars.util

import tanks.Team

fun teamColorToBuildingColor(team: Team?): Triple<Double, Double, Double> {
    val r = team?.teamColorR ?: 128.0
    val g = team?.teamColorG ?: 128.0
    val b = team?.teamColorB ?: 128.0

    return Triple(r, g, b)
}