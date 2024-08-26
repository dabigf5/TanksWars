package tools.important.tankswars

import main.Tanks
import tanks.Game
import tanks.Team
import tanks.extension.Extension
import tanks.gui.screen.ScreenGame
import tanks.tank.*
import tools.important.tankswars.building.*

fun fuck(team: Team) {
    News.sendMessage(
        "${teamColorText(team, team.name.upperFirst())} fled the battlefield!",
        if (team == Game.playerTank.team) NewsMessageType.BAD_THING_HAPPENED else NewsMessageType.GOOD_THING_HAPPENED
    )

    for (movable in Game.movables) {
        if (movable.team != team) continue

        if (movable is TankBuildingCapturable) {
            movable.capture(null)
            continue
        }

        if (movable is Explosion) {
            movable.damage = 0.0
            continue
        }

        if (movable is Mine) {
            movable.damage = 0.0
            movable.destroy = true
            continue
        }

        movable.destroy = true
    }
}

class TankFiller(name: String, x: Double, y: Double, angle: Double) : TankAIControlled(name,
    x,
    y,
    0.0,
    0.0,
    0.0,
    0.0,
    angle,
    ShootAI.none
)

fun register(tankClass: Class<out Tank>, name: String) {
    Game.registerTank(tankClass, name, 0.0)
}

class TanksWars : Extension("TanksWars") {
    override fun setUp() {
        register(TankFiller::class.java, "tw_filler1")
        register(TankFiller::class.java, "tw_filler2")
        register(TankFiller::class.java, "tw_filler3")

        register(TankOutpost::class.java, "tw_outpost")
        register(TankKeep::class.java, "tw_keep")
        register(TankKeepBase::class.java, "tw_keepbase")
        register(TankBarracks::class.java, "tw_barracks")
        register(TankHospital::class.java, "tw_hospital")
        register(TankSentry::class.java, "tw_sentry")

        register(TankFiller::class.java, "tw_filler4")

        register(TankSoldier::class.java, "tw_soldier")
        register(TankSoldierCaptain::class.java, "tw_soldiercaptain")
        register(TankSoldierDefender::class.java, "tw_soldierdefender")
    }

    override fun draw() {
        News.draw()
    }

    override fun update() {
        News.update()

        if (ScreenGame.finished) return

        val screen = Game.screen

        if (screen is ScreenGame)
            if (screen.paused) return

        for (movable in Game.movables) {
            val team = movable.team ?: continue

            if (movable !is Tank) continue
            if (movable is TankSoldier) continue
            if (movable is TankBuilding) continue

            if (!movable.destroy) continue
            if (movable.destroyTimer > 0.0) continue

            val wasCommander = movable is TankPlayer || movable.name.startsWith("cmd")

            val movableNameFormatted = teamColorText(team, movable.name
                .replace('_', ' ')
                .upperFirst())

            News.sendMessage(
                "$movableNameFormatted has been defeated!",

                if (Team.isAllied(movable, Game.playerTank))
                    NewsMessageType.BAD_THING_HAPPENED
                else
                    NewsMessageType.GOOD_THING_HAPPENED
            )

            if (wasCommander) fuck(team)
        }
    }
}


fun main() {
    Tanks.launchWithExtensions(arrayOf("debug"), arrayOf(TanksWars()), IntArray(0))
}

@Suppress("unused")
const val EXTENSION_VERSION = "Tanks Wars 0.1.1"