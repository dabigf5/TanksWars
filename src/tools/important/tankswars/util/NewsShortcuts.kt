package tools.important.tankswars.util

import tanks.Game
import tanks.Team
import tanks.gui.screen.ScreenPartyHost
import tanks.tank.Tank
import tanks.tank.TankPlayer
import tanks.tank.TankPlayerRemote
import tools.important.tankswars.building.BuildingType
import tools.important.tankswars.core.News
import tools.important.tankswars.core.NewsMessageType
import tools.important.tankswars.event.to_client.EventBuildingWasDestroyed
import tools.important.tankswars.event.to_client.EventTankDefeatMessage
import tools.important.tankswars.event.to_client.EventTeamFled

fun News.sendCaptureMessage(capturedTank: Tank, capturingTank: Tank?) {
    val buildingType = BuildingType.getBuildingTypeFromName(capturedTank.name)!!

    val buildingText = teamColoredText(capturedTank.team, buildingType.displayName.formatInternalName())
    val capturerText = teamColoredText(capturingTank?.team, capturingTank?.team?.name?.formatInternalName() ?: "No one")

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

    val buildingText = coloredText(destroyedColor, buildingType.displayName)
    val destroyerText = coloredText(destroyerColor, destroyerTeamName)

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
        "${coloredText(teamColor, teamName)} fled the battlefield!",
        if (teamName == Game.playerTank.team.name) // fixme
            NewsMessageType.BAD_THING_HAPPENED
        else
            NewsMessageType.GOOD_THING_HAPPENED
    )
}

fun News.sendDefeatMessage(name: String, color: Color, allied: Boolean) {
    val formattedName = coloredText(color, name)

    sendMessage(
        "$formattedName has been defeated!",

        if (allied)
            NewsMessageType.BAD_THING_HAPPENED
        else
            NewsMessageType.GOOD_THING_HAPPENED
    )
}

fun News.broadcastDefeatMessage(tank: Tank) {
    val broadcastName = when (tank) {
        is TankPlayer -> tank.player.username
        is TankPlayerRemote -> tank.player.username
        else -> tank.name.formatInternalName()
    }

    sendDefeatMessage(
        broadcastName,
        getTeamColorOrGray(tank.team),
        Team.isAllied(tank, Game.playerTank)
    )

    if (!ScreenPartyHost.isServer) return

    for (connection in ScreenPartyHost.server.connections) {
        val (r, g, b) = getTeamColorOrGray(tank.team)

        connection.events.add(
            EventTankDefeatMessage(
                broadcastName,
                r.toInt(), g.toInt(), b.toInt(),
                Team.isAllied(tank, connection.player.tank)
            )
        )
    }
}

fun News.broadcastFleeMessage(team: Team) {
    val teamNameFormatted = team.name.formatInternalName()
    val color = getTeamColorOrGray(team)
    sendFleeMessage(teamNameFormatted, color)
    if (!ScreenPartyHost.isServer) return

    Game.eventsOut.add(EventTeamFled(teamNameFormatted, color))
}

fun News.broadcastDestroyMessage(tank: Tank, destroyer: Tank?) {
    sendDestroyMessage(tank, destroyer)

    if (!ScreenPartyHost.isServer) return

    for (connection in ScreenPartyHost.server.connections) {
        val (r, g, b) = getTeamColorOrGray(tank.team)
        val (dr, dg, db) = getTeamColorOrGray(destroyer?.team)
        connection.events.add(
            EventBuildingWasDestroyed(
                tank.name,
                r.toInt(),
                g.toInt(),
                b.toInt(),
                getTeamNameFromDestroyer(destroyer),
                dr.toInt(),
                dg.toInt(),
                db.toInt(),
                Team.isAllied(tank, connection.player.tank)
            )
        )
    }
}