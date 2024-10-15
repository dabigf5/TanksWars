package tools.important.tankswars.building

import tanks.Game
import tanks.tank.Tank
import tools.important.tankswars.building.tank.*
import tools.important.tankswars.core.News

/**
 * A class defining building capture behavior.
 */
class CaptureProperties(
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


/**
 * An enum class whose entries contain shared or client-sided constant metadata and functions associated with buildings.
 */
enum class BuildingType(
    /**
     * The name displayed ingame above the building.
     */
    val displayName: String,

    /**
     * The name the tankClass is registered with when the extension's setUp is called.
     */
    val registryName: String,

    /**
     * The description that will be assigned to instances on creation
     */
    val description: String,

    /**
     * The tank class associated with the building type.
     */
    val tankClass: Class<out TankBuilding>,

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

    val onSharedDraw: ((Tank) -> Unit)? = null,
    val onSharedUpdate: ((Tank) -> Unit)? = null,
    val onSharedPreUpdate: ((Tank) -> Unit)? = null,
) {
    // this enum does not directly define lambdas in the constructor parameters due to a bug in intellij that makes debugging a pain in the nuts
    // https://youtrack.jetbrains.com/issue/IDEA-305703/Debugger-Breakpoints-in-lambda-functions-in-enum-constants-constructor-are-ignored
    KEEP(
        displayName = "Keep",
        registryName = "tw_keep",
        description = "A fortified keep that will spawn defensive tanks",
        tankClass = TankKeep::class.java,

        health = 8.0,

        stationary = true,
        captureProperties = CaptureProperties.noFunction,
        spawnChance = 0.008,

        onSharedDraw = keepSharedDraw,
        onSharedUpdate = keepSharedUpdate
    ),
    KEEP_BASE(
        displayName = "Base Keep",
        registryName = "tw_keepbase",
        description = "A keep that will cause its team to flee if it is captured",
        tankClass = TankKeepBase::class.java,

        health = 12.0,

        stationary = true,
        captureProperties = CaptureProperties.noFunction,
        spawnChance = 0.008,

        onSharedDraw = keepSharedDraw,
        onSharedUpdate = keepSharedUpdate
    ),
    BARRACKS(
        displayName = "Barracks",
        registryName = "tw_barracks",
        description = "Barracks that will train and send out offensive soldiers",
        tankClass = TankBarracks::class.java,

        health = 5.0,

        stationary = true,
        captureProperties = CaptureProperties.noFunction,
        spawnChance = 0.02
    ),
    OUTPOST(
        displayName = "Outpost",
        registryName = "tw_outpost",
        description = "Small outpost that spawns a few defensive tanks",
        tankClass = TankOutpost::class.java,

        health = 5.0,

        stationary = true,
        captureProperties = CaptureProperties.noFunction,
        spawnChance = 0.06
    ),
    SENTRY(
        displayName = "Sentry Gun",
        registryName = "tw_sentry",
        description = "A sentry gun that will fire at enemy tanks in sight",
        tankClass = TankSentry::class.java,

        health = 5.0,

        stationary = true,
    )
    ;

    companion object {
        fun getBuildingTypeFromClass(buildingClass: Class<out Tank>): BuildingType? {
            for (buildingType in BuildingType.entries) {
                if (buildingType.tankClass == buildingClass) {
                    return buildingType
                }
            }

            return null
        }

        fun getBuildingTypeFromName(name: String): BuildingType? {
            for (tankEntry in Game.registryTank.tankEntries) {
                if (tankEntry.name == name) {
                    return getBuildingTypeFromClass(tankEntry.tank)
                }
            }

            return null
        }
    }
}