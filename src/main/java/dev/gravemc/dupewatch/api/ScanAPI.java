package dev.gravemc.dupewatch.api;

import dev.gravemc.dupewatch.api.model.DWScanResult;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * API for triggering and querying DupeWatch inventory scans.
 *
 * <p>Obtain an instance via {@link DupeWatchAPI#scanning()}.
 *
 * <h2>Example</h2>
 * <pre>{@code
 * ScanAPI scan = DupeWatchAPI.get().scanning();
 *
 * scan.scanPlayer(player.getUniqueId()).thenAccept(result -> {
 *     if (result.isTriggered()) {
 *         getLogger().info(player.getName() + " was flagged by " + result.getFlaggedRuleLabels());
 *     }
 * });
 * }</pre>
 */
public interface ScanAPI {

    /**
     * Trigger an asynchronous inventory scan for a player.
     *
     * <p>The scan is queued through DupeWatch's normal scan pipeline and respects
     * rate limits, cooldowns, and concurrency limits configured in {@code config.yml}.
     *
     * <p>The returned {@link CompletableFuture} completes when the scan finishes.
     * If the player is offline or scanning is disabled, the future completes with
     * a non-triggered result.
     *
     * @param playerUuid the UUID of the player to scan
     * @return a future that completes with the scan result
     */
    CompletableFuture<DWScanResult> scanPlayer(UUID playerUuid);

    /**
     * Check whether DupeWatch scanning is currently enabled.
     * <p>Scanning can be disabled via {@code config.yml}.
     *
     * @return {@code true} if scanning is active
     */
    boolean isScanningEnabled();

    /**
     * Get the maximum number of concurrent scans configured in DupeWatch.
     *
     * @return max concurrent scan count
     */
    int getMaxConcurrentScans();
}
