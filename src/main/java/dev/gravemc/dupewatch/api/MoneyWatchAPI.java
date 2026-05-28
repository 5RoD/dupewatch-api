package dev.gravemc.dupewatch.api;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * API for querying DupeWatch's Money Watch system (Vault integration).
 *
 * <p>Money Watch monitors player economy activity and flags sudden balance spikes
 * or abnormal growth rates that are consistent with economy duplication exploits.
 *
 * <p>Obtain an instance via {@link DupeWatchAPI#moneyWatch()}.
 *
 * <h2>Example</h2>
 * <pre>{@code
 * MoneyWatchAPI mw = DupeWatchAPI.get().moneyWatch();
 *
 * if (mw.isEnabled()) {
 *     mw.hasRecentAnomalies(player.getUniqueId()).thenAccept(flagged -> {
 *         if (flagged) {
 *             player.sendMessage("§c[Admin] Your economy activity has been flagged.");
 *         }
 *     });
 * }
 * }</pre>
 */
public interface MoneyWatchAPI {

    /**
     * Check whether DupeWatch's Money Watch system is enabled.
     * <p>Requires Vault to be installed and enabled via {@code config.yml}.
     *
     * @return {@code true} if Money Watch is active
     */
    boolean isEnabled();

    /**
     * Check whether a player has had any recent economy anomalies detected by DupeWatch.
     *
     * <p>An anomaly includes balance doubling, exponential growth, or sudden spikes
     * that exceed configured thresholds.
     *
     * @param playerUuid the player's UUID
     * @return a future with {@code true} if the player has recent anomalies
     */
    CompletableFuture<Boolean> hasRecentAnomalies(UUID playerUuid);

    /**
     * Get the player's most recently recorded balance as tracked by DupeWatch.
     *
     * <p>Returns {@code -1.0} if no balance history is available for the player.
     *
     * @param playerUuid the player's UUID
     * @return a future with the last recorded balance, or {@code -1.0} if unavailable
     */
    CompletableFuture<Double> getLastRecordedBalance(UUID playerUuid);
}
