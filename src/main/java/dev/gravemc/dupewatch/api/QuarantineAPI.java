package dev.gravemc.dupewatch.api;

import dev.gravemc.dupewatch.api.model.DWQuarantinedItem;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * API for managing DupeWatch's quarantine system.
 *
 * <p>Quarantine mode stores confiscated items in the database rather than
 * deleting them, allowing staff to review and return them later.
 *
 * <p>Obtain an instance via {@link DupeWatchAPI#quarantine()}.
 *
 * <h2>Example</h2>
 * <pre>{@code
 * QuarantineAPI quarantine = DupeWatchAPI.get().quarantine();
 *
 * // Get all items quarantined from a player
 * quarantine.getQuarantinedItems(player.getUniqueId()).thenAccept(items -> {
 *     items.forEach(item -> getLogger().info(item.getReason()));
 * });
 *
 * // Return a specific quarantined item
 * quarantine.returnItem("QUARANTINE_ID", "AdminName");
 * }</pre>
 */
public interface QuarantineAPI {

    /**
     * Check whether the quarantine system is available and enabled.
     *
     * <p>Quarantine requires {@code confiscation.mode: QUARANTINE} in {@code config.yml}.
     *
     * @return {@code true} if quarantine is active
     */
    boolean isAvailable();

    /**
     * Get all quarantined items for a specific player.
     *
     * @param playerUuid the player's UUID
     * @return a future with the list of quarantined items (may be empty)
     */
    CompletableFuture<List<DWQuarantinedItem>> getQuarantinedItems(UUID playerUuid);

    /**
     * Get all currently quarantined items across all players.
     *
     * @return a future with all quarantined items
     */
    CompletableFuture<List<DWQuarantinedItem>> getAllQuarantinedItems();

    /**
     * Return a specific quarantined item to its original owner.
     *
     * <p>If the player is currently online, the item is delivered immediately.
     * If the player is offline, it is queued for delivery on next login.
     *
     * @param quarantineId the quarantine ID of the item to return
     * @param staffName    the name of the staff member performing the return
     * @return a future that completes with {@code true} on success
     */
    CompletableFuture<Boolean> returnItem(String quarantineId, String staffName);

    /**
     * Permanently delete a quarantined item.
     * <p><strong>Warning:</strong> This is irreversible.
     *
     * @param quarantineId the quarantine ID of the item to delete
     * @param staffName    the name of the staff member performing the deletion
     * @return a future that completes with {@code true} on success
     */
    CompletableFuture<Boolean> deleteItem(String quarantineId, String staffName);

    /**
     * Return all quarantined items for a specific player.
     *
     * @param playerUuid the player's UUID
     * @param staffName  the name of the staff member performing the return
     * @return a future with the number of items returned (or queued for return)
     */
    CompletableFuture<Integer> returnAllForPlayer(UUID playerUuid, String staffName);

    /**
     * Get the total number of currently quarantined items.
     *
     * @return a future with the count
     */
    CompletableFuture<Long> countQuarantined();
}
