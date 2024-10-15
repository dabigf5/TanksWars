package tools.important.tankswars.util

import tanks.Game
import tanks.Team
import tanks.tank.Tank
import tools.important.tankswars.building.BuildingType
import tools.important.tankswars.core.News
import tools.important.tankswars.core.NewsMessageType

fun News.sendCaptureMessage(capturedTank: Tank, capturingTank: Tank?) {
    val buildingType = BuildingType.getBuildingTypeFromName(capturedTank.name)!!

    val buildingText = teamColoredText(capturedTank.team, buildingType.displayName.formatInternalName())
    val capturerText = teamColoredText(capturingTank?.team, capturingTank?.team?.name?.formatInternalName()?:"No one")

    sendMessage(
        "$buildingText was captured by $capturerText",
        if (Team.isAllied(Game.playerTank, capturedTank))
            NewsMessageType.CAPTURE_BAD
        else
            if (Team.isAllied(Game.playerTank, capturingTank))
                NewsMessageType.CAPTURE_GOOD
            else
                NewsMessageType.CAPTURE_NEUTRAL
    )
}

fun getTeamNameFromDestroyer(destroyer: Tank?): String {
    return destroyer?.team?.name ?: destroyer?.name ?: "No one"
}

fun News.sendDestroyMessage(tank: Tank, destroyer: Tank?) {
    sendDestroyMessage(
        getTeamColorOrGray(tank.team), tank.name,
        getTeamNameFromDestroyer(destroyer), getTeamColorOrGray(destroyer?.team),
        Team.isAllied(tank, destroyer)
    )
}

fun News.sendDestroyMessage(
    destroyedColor: Color, destroyedName: String,
    destroyerTeamName: String, destroyerColor: Color,
    allied: Boolean
) {
    val buildingType = BuildingType.getBuildingTypeFromName(destroyedName)!!

    val buildingText = coloredText(destroyedColor, buildingType.displayName.formatInternalName())
    val destroyerText = coloredText(destroyerColor, destroyerTeamName.formatInternalName())

    sendMessage(
        "$buildingText was destroyed by $destroyerText",
        if (allied)
            NewsMessageType.BAD_THING_HAPPENED
        else
            NewsMessageType.GOOD_THING_HAPPENED
    )
}

fun News.sendFleeMessage(teamName: String, teamColor: Color) {
    sendMessage(
        "${coloredText(teamColor, teamName.formatInternalName())} fled the battlefield!",
        if (teamName == Game.playerTank.team.name)
            NewsMessageType.BAD_THING_HAPPENED
        else
            NewsMessageType.GOOD_THING_HAPPENED
    )
}

fun News.sendDefeatMessage(tank: Tank) {
    sendDefeatMessage(tank.name, tank.team.teamColor, Team.isAllied(tank, Game.playerTank))
}

fun News.sendDefeatMessage(name: String, color: Color, allied: Boolean) {
    val formattedName = coloredText(color, name.formatInternalName())

    sendMessage(
        "$formattedName has been defeated!",

        if (allied)
            NewsMessageType.BAD_THING_HAPPENED
        else
            NewsMessageType.GOOD_THING_HAPPENED
    )
}