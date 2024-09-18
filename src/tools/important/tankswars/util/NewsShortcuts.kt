package tools.important.tankswars.util

import tanks.Game
import tanks.Team
import tanks.tank.Tank
import tools.important.tankswars.building.BuildingType
import tools.important.tankswars.core.News
import tools.important.tankswars.core.NewsMessageType

fun News.sendCaptureMessage(capturedTank: Tank, capturingTank: Tank?) {
    val buildingType = BuildingType.getBuildingTypeFromName(capturedTank)!!

    val buildingText = teamColorText(capturedTank.team, buildingType.displayName.formatInternalName())
    val capturerText = teamColorText(capturingTank?.team, capturingTank?.name?.formatInternalName()?:"")

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