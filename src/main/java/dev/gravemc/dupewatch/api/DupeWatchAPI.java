package dev.gravemc.dupewatch.api;

import dev.gravemc.dupewatch.DupeWatchPlugin;
import dev.gravemc.dupewatch.api.impl.*;

/**
 * Main entry point for the DupeWatch Plugin API.
 *
 * <p>Use {@link #get()} to obtain the singleton API instance, then call the
 * sub-APIs for each domain:
 *
 * <pre>{@code
 * DupeWatchAPI api = DupeWatchAPI.get();
 *
 * // Case management
 * api.cases().getActiveCases();
 *
 * // Watchlist rules
 * api.watchlist().getRules();
 *
 * // Scanning
 * api.scanning().scanPlayer(player.getUniqueId());
 *
 * // Quarantine
 * api.quarantine().getQuarantinedItems(player.getUniqueId());
 *
 * // Player risk
 * api.risk().getRiskLevel(player.getUniqueId());
 *
 * // Money Watch
 * api.moneyWatch().hasRecentAnomalies(player.getUniqueId());
 *
 * // Statistics
 * api.stats().getActiveCaseCount();
 * }</pre>
 *
 * <h2>Events</h2>
 * <p>You can also listen to DupeWatch events using Bukkit's event system:
 * <pre>{@code
 * @EventHandler
 * public void onPlayerFlagged(DWPlayerFlaggedEvent event) {
 *     // ... your logic here
 * }
 * }</pre>
 *
 * <h2>Setup</h2>
 * <p>Add DupeWatch as a dependency in your {@code plugin.yml}:
 * <pre>{@code
 * depend:
 *   - DupeWatch
 * }</pre>
 *
 * <p>Or as a soft dependency (DupeWatch optional):
 * <pre>{@code
 * softdepend:
 *   - DupeWatch
 * }</pre>
 *
 * <p>Then in your code, check if DupeWatch is loaded before using the API:
 * <pre>{@code
 * if (Bukkit.getPluginManager().getPlugin("DupeWatch") != null) {
 *     DupeWatchAPI api = DupeWatchAPI.get();
 *     // use the API
 * }
 * }</pre>
 *
 * @see CaseAPI
 * @see WatchlistAPI
 * @see ScanAPI
 * @see QuarantineAPI
 * @see PlayerRiskAPI
 * @see MoneyWatchAPI
 * @see StatsAPI
 */
public final class DupeWatchAPI {

    private static DupeWatchAPI instance;

    private final CaseAPI caseAPI;
    private final WatchlistAPI watchlistAPI;
    private final ScanAPI scanAPI;
    private final QuarantineAPI quarantineAPI;
    private final PlayerRiskAPI riskAPI;
    private final MoneyWatchAPI moneyWatchAPI;
    private final StatsAPI statsAPI;

    private DupeWatchAPI(DupeWatchPlugin plugin) {
        this.caseAPI = new CaseAPIImpl(plugin);
        this.watchlistAPI = new WatchlistAPIImpl(plugin);
        this.scanAPI = new ScanAPIImpl(plugin);
        this.quarantineAPI = new QuarantineAPIImpl(plugin);
        this.riskAPI = new PlayerRiskAPIImpl(plugin);
        this.moneyWatchAPI = new MoneyWatchAPIImpl(plugin);
        this.statsAPI = new StatsAPIImpl(plugin);
    }

    /**
     * Get the singleton DupeWatchAPI instance.
     *
     * <p>Only available after DupeWatch has fully loaded (after {@code onEnable}).
     *
     * @return the API instance
     * @throws IllegalStateException if DupeWatch has not been initialized yet
     */
    public static DupeWatchAPI get() {
        if (instance == null) {
            throw new IllegalStateException(
                    "DupeWatchAPI is not initialized. " +
                    "Ensure DupeWatch is loaded before calling DupeWatchAPI.get(). " +
                    "Add 'depend: [DupeWatch]' or 'softdepend: [DupeWatch]' to your plugin.yml.");
        }
        return instance;
    }

    /**
     * Returns {@code true} if the API has been initialized and is ready to use.
     *
     * <p>Useful when DupeWatch is a soft dependency:
     * <pre>{@code
     * if (DupeWatchAPI.isAvailable()) {
     *     DupeWatchAPI.get().cases().getActiveCases();
     * }
     * }</pre>
     *
     * @return whether the API is available
     */
    public static boolean isAvailable() {
        return instance != null;
    }

    /**
     * Initialize the API. Called internally by {@link DupeWatchPlugin#onEnable()}.
     * Do not call this yourself.
     *
     * @param plugin the DupeWatch plugin instance
     */
    public static void initialize(DupeWatchPlugin plugin) {
        if (instance != null) {
            throw new IllegalStateException("DupeWatchAPI is already initialized.");
        }
        instance = new DupeWatchAPI(plugin);
        plugin.getLogger().info("[DupeWatchAPI] API initialized. Version: " + plugin.getDescription().getVersion());
    }

    /**
     * Shut down the API. Called internally by {@link DupeWatchPlugin#onDisable()}.
     * Do not call this yourself.
     */
    public static void shutdown() {
        instance = null;
    }

    // ── Domain APIs ──────────────────────────────────────────────────────────

    /**
     * API for querying and managing investigation cases.
     *
     * @return the {@link CaseAPI}
     */
    public CaseAPI cases() { return caseAPI; }

    /**
     * API for querying and managing watchlist rules.
     *
     * @return the {@link WatchlistAPI}
     */
    public WatchlistAPI watchlist() { return watchlistAPI; }

    /**
     * API for triggering player inventory scans.
     *
     * @return the {@link ScanAPI}
     */
    public ScanAPI scanning() { return scanAPI; }

    /**
     * API for managing the quarantine system.
     *
     * @return the {@link QuarantineAPI}
     */
    public QuarantineAPI quarantine() { return quarantineAPI; }

    /**
     * API for querying player risk scores and levels.
     *
     * @return the {@link PlayerRiskAPI}
     */
    public PlayerRiskAPI risk() { return riskAPI; }

    /**
     * API for querying economy anomaly data (Money Watch).
     *
     * @return the {@link MoneyWatchAPI}
     */
    public MoneyWatchAPI moneyWatch() { return moneyWatchAPI; }

    /**
     * API for querying server-wide DupeWatch statistics.
     *
     * @return the {@link StatsAPI}
     */
    public StatsAPI stats() { return statsAPI; }

    /**
     * Get the installed DupeWatch version string.
     *
     * @return version string, e.g., {@code "1.3.4"}
     */
    public String getVersion() { return statsAPI.getPluginVersion(); }
}
