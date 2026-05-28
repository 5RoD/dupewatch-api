package dev.gravemc.dupewatch.api;

import dev.gravemc.dupewatch.api.model.DWRiskLevel;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * API for querying a player's DupeWatch risk score and level.
 *
 * <p>Risk scores are computed by DupeWatch's ML system and increase each time
 * a player is flagged. They decay over time (configurable via {@code config.yml}).
 *
 * <p>Obtain an instance via {@link DupeWatchAPI#risk()}.
 *
 * <h2>Example — Block access to shop for risky players</h2>
 * <pre>{@code
 * PlayerRiskAPI risk = DupeWatchAPI.get().risk();
 *
 * risk.getRiskLevel(player.getUniqueId()).thenAccept(level -> {
 *     if (level == DWRiskLevel.HIGH || level == DWRiskLevel.EXTREME) {
 *         player.sendMessage("§cYour account has been flagged. Shop access denied.");
 *         event.setCancelled(true);
 *     }
 * });
 * }</pre>
 */
public interface PlayerRiskAPI {

    /**
     * Get a player's current numeric risk score.
     *
     * <p>Scores are cumulative: each flag adds the ML detection confidence (0.0–1.0).
     * A score of {@code 5.0} or above is considered extreme.
     *
     * @param playerUuid the player's UUID
     * @return a future with the risk score (0.0 if no record exists)
     */
    CompletableFuture<Double> getRiskScore(UUID playerUuid);

    /**
     * Get a player's risk level as a categorized {@link DWRiskLevel} enum value.
     *
     * @param playerUuid the player's UUID
     * @return a future with the risk level (never {@code null})
     */
    CompletableFuture<DWRiskLevel> getRiskLevel(UUID playerUuid);

    /**
     * Check whether a player currently has any active (unresolved) DupeWatch cases.
     *
     * <p>This is a quick synchronous check against the in-memory case manager.
     *
     * @param playerUuid the player's UUID
     * @return {@code true} if the player has at least one active case
     */
    boolean isPlayerFlagged(UUID playerUuid);

    /**
     * Get a formatted display string for a player's risk score.
     *
     * <p>The string includes a color code and label, e.g., {@code "§e2.3 (Moderate)"}.
     * Useful for staff messages and command output.
     *
     * @param playerUuid the player's UUID
     * @return a future with the formatted risk string
     */
    CompletableFuture<String> getFormattedRiskScore(UUID playerUuid);
}
