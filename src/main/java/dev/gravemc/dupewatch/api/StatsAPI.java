package dev.gravemc.dupewatch.api;

/**
 * API for querying DupeWatch server-wide statistics and metrics.
 *
 * <p>Obtain an instance via {@link DupeWatchAPI#stats()}.
 *
 * <h2>Example</h2>
 * <pre>{@code
 * StatsAPI stats = DupeWatchAPI.get().stats();
 *
 * getLogger().info("DupeWatch v" + stats.getPluginVersion());
 * getLogger().info("Active cases: " + stats.getActiveCaseCount());
 * getLogger().info("Total rules: " + stats.getRuleCount());
 * }</pre>
 */
public interface StatsAPI {

    /**
     * Get the currently installed DupeWatch plugin version string.
     *
     * @return version string (e.g., {@code "1.3.4"})
     */
    String getPluginVersion();

    /**
     * Get the number of currently active (unresolved) investigation cases.
     *
     * @return active case count
     */
    long getActiveCaseCount();

    /**
     * Get the number of resolved investigation cases.
     *
     * @return resolved case count
     */
    long getResolvedCaseCount();

    /**
     * Get the total number of cases (active + resolved).
     *
     * @return total case count
     */
    long getTotalCaseCount();

    /**
     * Get the number of watchlist rules currently loaded.
     *
     * @return rule count
     */
    int getRuleCount();

    /**
     * Get the number of items currently in quarantine.
     * <p>Returns {@code 0} if quarantine is not enabled.
     *
     * @return quarantined item count
     */
    long getQuarantinedItemCount();

    /**
     * Check whether the DupeWatch API is fully initialized and ready to use.
     *
     * @return {@code true} if the API is ready
     */
    boolean isReady();
}
