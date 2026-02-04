package tools.important.tankswars.building

import tanks.Game
import tanks.tank.Tank
import tools.important.tankswars.building.tank.*
import tools.important.tankswars.core.News
import tools.important.tankswars.tank.TankSoldier
import tools.important.tankswars.tank.TankSoldierCaptain
import tools.important.tankswars.tank.TankSoldierDefender

/**
 * A class defining building capture behavior.
 */
data class CaptureProperties(
    /**
     * Invoked when the building is captured, before its team is changed.
     *
     * The parameter is the tank that captured the building.
     */
    val onSharedCapture: ((Tank) -> Unit)?,
) {
    companion object {
        val noFunction = CaptureProperties(null)
    }
}

data class BuildingProperties(
    /**
     * The name displayed ingame above the building.
     */
    val displayName: String,

    /**
     * Whether or not this building is immovable.
     */
    val stationary: Boolean = false,

    /**
     * The health value that is assigned to instances when spawned, and when captured.
     */
    val health: Double = 1.0,

    /**
     * A value that defines this building's capture behavior.
     *
     * If this is not null, the building will be invulnerable, it will get captured when it would normally die,
     * its health will be restored to `health`, and a 'this building was captured' message will appear in the news.
     *
     * If this is null, the building can be killed just like any other tank, and a
     * 'this building was destroyed' message will appear in the news.
     *
     * @see CaptureProperties
     * @see News
     * @see health
     */
    val captureProperties: CaptureProperties? = null,

    /**
     * A value that defines this building's spawn chance when it currently has a team.
     *
     * If the building does not have a team, its spawn chance will be reset to 0.
     */
    val spawnChance: Double? = null,
)

data class SoldierProperties(
    val commandable: Boolean
)

/**
 * An enum class whose entries contain shared or client-sided constant metadata and functions associated with buildings.
 */
enum class TwTankType(
    /**
     * Building Properties for this tank type, if it is a building.
     * If not null, this tank type is considered a building. If null, this tank type is not considered a building.
     */
    val buildingProperties: BuildingProperties? = null,

    /**
     * Soldier Properties for this tank type, if it is a soldier.
     * If not null, this tank type is considered a soldier. If null, this tank type is not considered a soldier.
     * This should not be non-null at the same time BuildingProperties is non-null.
     */
    val soldierProperties: SoldierProperties? = null,

    /**
     * The name the tankClass is registered with when the extension's setUp is called.
     */
    val registryName: String,

    /**
     * The description that will be assigned to instances on creation
     */
    val description: String,

    /**
     * The tank class associated with the tank type.
     */
    val tankClass: Class<out Tank>,

    val onSharedDraw: ((Tank) -> Unit)? = null,
    val onSharedUpdate: ((Tank) -> Unit)? = null,
    val onSharedPreUpdate: ((Tank) -> Unit)? = null,
) {
    SOLDIER(
        soldierProperties = SoldierProperties(
            commandable = true,
        ),
        tankClass = TankSoldier::class.java,
        registryName = "tw_soldier",
        description = "An offensive soldier who will seek out enemies",
    ),

    SOLDIER_CAPTAIN(
        soldierProperties = SoldierProperties(
            commandable = true,
        ),
        tankClass = TankSoldierCaptain::class.java,
        registryName = "tw_soldiercaptain",
        description = "A slightly stronger captain of offensive soldiers",
    ),

    DEFENSIVE_SOLDIER(
        soldierProperties = SoldierProperties(
            commandable = true,
        ),
        tankClass = TankSoldierDefender::class.java,
        registryName = "tw_soldierdefender",
        description = "A defensive soldier who will defend the tank that spawned them",
    ),

    // this enum does not directly define lambdas in the constructor parameters due to a bug in intellij that makes debugging a pain in the nuts
    // https://youtrack.jetbrains.com/issue/IDEA-305703/Debugger-Breakpoints-in-lambda-functions-in-enum-constants-constructor-are-ignored
    KEEP(
        buildingProperties = BuildingProperties(
            displayName = "Keep",
            health = 8.0,
            stationary = true,
            captureProperties = CaptureProperties.noFunction,
            spawnChance = 0.008,
        ),

        registryName = "tw_keep",
        description = "A fortified keep that will spawn defensive tanks",
        tankClass = TankKeep::class.java,

        onSharedDraw = keepSharedDraw,
        onSharedUpdate = keepSharedUpdate
    ),
    KEEP_BASE(
        buildingProperties = BuildingProperties(
            displayName = "Base Keep",
            health = 12.0,

            stationary = true,
            captureProperties = CaptureProperties.noFunction,
            spawnChance = 0.008,
        ),

        registryName = "tw_keepbase",
        description = "A keep that will cause its team to flee if it is captured",
        tankClass = TankKeepBase::class.java,

        onSharedDraw = keepSharedDraw,
        onSharedUpdate = keepSharedUpdate
    ),
    BARRACKS(
        buildingProperties = BuildingProperties(
            displayName = "Barracks",
            health = 5.0,

            stationary = true,
            captureProperties = CaptureProperties.noFunction,
            spawnChance = 0.02
        ),

        registryName = "tw_barracks",
        description = "Barracks that will train and send out offensive soldiers",
        tankClass = TankBarracks::class.java,
    ),
    OUTPOST(
        buildingProperties = BuildingProperties(
            displayName = "Outpost",
            health = 5.0,

            stationary = true,
            captureProperties = CaptureProperties.noFunction,
            spawnChance = 0.06
        ),

        registryName = "tw_outpost",
        description = "Small outpost that spawns a few defensive tanks",
        tankClass = TankOutpost::class.java,
    ),
    SENTRY(
        buildingProperties = BuildingProperties(
            displayName = "Sentry Gun",
            health = 8.0,

            stationary = true,
        ),

        registryName = "tw_sentry",
        description = "An armored sentry gun that will fire at enemy tanks in sight",
        tankClass = TankSentry::class.java,
    ),
    ;

    companion object {
        fun getTankTypeFromClass(tankClass: Class<out Tank>): TwTankType? {
            for (buildingType in TwTankType.entries) {
                if (buildingType.tankClass == tankClass) {
                    return buildingType
                }
            }

            return null
        }

        fun getTankTypeFromName(name: String): TwTankType? {
            for (tankEntry in Game.registryTank.tankEntries) {
                if (tankEntry.name == name) {
                    return getTankTypeFromClass(tankEntry.tank)
                }
            }

            return null
        }
    }
}