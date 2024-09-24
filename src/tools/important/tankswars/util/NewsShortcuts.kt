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

fun News.sendFleeMessage(teamName: String, teamColor: Color) {
    sendMessage(
        "${coloredText(teamColor, teamName.formatInternalName())} fled the battlefield!",
        if (teamName == Game.playerTank.team.name)
            NewsMessageType.BAD_THING_HAPPENED
        else
            NewsMessageType.GOOD_THING_HAPPENED
    )
}