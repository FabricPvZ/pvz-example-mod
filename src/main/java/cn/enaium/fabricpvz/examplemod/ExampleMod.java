package cn.enaium.fabricpvz.examplemod;

import cn.enaium.fabricpvz.api.lawn.LawnEvents;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Example mod entrypoint (Fabric Loader's {@link ModInitializer}).
 *
 * <p>Demonstrates the two halves of the API a mod author uses:
 * <ul>
 *   <li>listening to gameplay events ({@link LawnEvents}) — the {@code SUN_COLLECTED} listener below
 *       doubles the sun collected by the player, and the {@code PLANT_PLACED} / {@code ZOMBIE_DEATH}
 *       listeners only record statistics;</li>
 *   <li>mixing into the game (see {@code cn.enaium.fabricpvz.examplemod.mixin}) — {@code LawnCommonMixin}
 *       proves the transform ran, {@code BoardMixin} reads a shadowed board field on every tick and
 *       {@code BoardAccessor} exposes the board's sun counter through a generated accessor.</li>
 * </ul>
 *
 * <p>Logging is the third thing it shows, and the one every mod needs: a mod logs through the SLF4J
 * API, whose Log4j backend and configuration the launcher publishes, so this mod's lines, Fabric
 * Loader's own lines and the Mixin subsystem's lines are one stream in one format. Nothing has to be
 * declared for it - the toolchain puts the SLF4J API on the compile class path
 * ({@code org.slf4j:slf4j-api}) and {@code fabric-provider} puts the implementation on the launch
 * class path.
 */
public final class ExampleMod implements ModInitializer {

    /** The mod's logger, named by the mod id, which is what its log lines are tagged with. */
    private static final Logger LOGGER = LoggerFactory.getLogger("pvz-example-mod");

    /** Set by {@code LawnCommonMixin} so tests can prove the bytecode transform actually ran. */
    public static boolean mixinApplied;

    /** Incremented by {@code BoardMixin} on every board tick. */
    public static int boardUpdates;

    /** Last sun amount observed through the shadowed {@code Board.mSunMoney}. */
    public static int lastObservedSun;

    /** Statistics used by the integration test. */
    public static int sunEvents;

    public static int plantsPlaced;

    public static int zombiesKilled;

    public static int levelStarts;

    @Override
    public void onInitialize() {
        LOGGER.info("Hello Fabric world!");

        // No check on the API's own initialisation state here: Fabric Loader defines no order
        // between mod entry points (it sorts mods by id in production and deliberately shuffles them
        // in a development environment), so a mod must not assume that another mod's `main` entry
        // point already ran. The event bus below is order independent: the API's bridge dispatches to
        // whatever listeners are registered, whenever they register.
        LawnEvents.SUN_COLLECTED.register((board, amount, source) -> {
            sunEvents++;
            return amount * 2;
        });
        LawnEvents.PLANT_PLACED.register((board, plant, gridX, gridY) -> plantsPlaced++);
        LawnEvents.ZOMBIE_DEATH.register((board, zombie) -> zombiesKilled++);
        LawnEvents.LEVEL_START.register((board, level, gameMode, background) -> levelStarts++);
    }
}
