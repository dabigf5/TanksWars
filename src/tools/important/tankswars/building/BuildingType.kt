package tools.important.tankswars.building

import tanks.Game
import tanks.tank.Tank
import tools.important.tankswars.building.tank.TankBuilding
import tools.important.tankswars.building.tank.TankKeep
import tools.important.tankswars.core.News

/**
 * An enum class whose entries contain shared or client-sided constant information about the buildings.
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
     * Whether or not this building can be captured.
     *
     * If this is true, the building will be invulnerable, it will get captured when it would normally die,
     * its health will be restored to `health`, and a 'this building was captured' message will appear in the news.
     *
     * If this is false, the building can be killed just like any other tank, and a
     * 'this building was destroyed' message will appear in the news.
     *
     * @see News
     * @see health
     */
    val capturable: Boolean = false,

    val onDraw: ((Tank) -> Unit)? = null,
) {
    KEEP(
        displayName = "Keep",
        registryName = "tw_keep",
        description = "A fortified keep that will spawn defensive tanks",
        tankClass = TankKeep::class.java,

        health = 4.0,

        stationary = true,
        capturable = true,
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
        fun getBuildingTypeFromName(tank: Tank): BuildingType? {
            for (tankEntry in Game.registryTank.tankEntries) {
                if (tankEntry.name == tank.name) {
                    return getBuildingTypeFromClass(tankEntry.tank)
                }
            }

            return null
        }
    }
}