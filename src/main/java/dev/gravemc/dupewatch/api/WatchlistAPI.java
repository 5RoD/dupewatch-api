package dev.gravemc.dupewatch.api;

import dev.gravemc.dupewatch.api.model.DWRule;

import java.util.List;
import java.util.Optional;

/**
 * API for querying and managing DupeWatch watchlist rules.
 *
 * <p>Obtain an instance via {@link DupeWatchAPI#watchlist()}.
 *
 * <h2>Example</h2>
 * <pre>{@code
 * WatchlistAPI watchlist = DupeWatchAPI.get().watchlist();
 *
 * // List all rules
 * for (DWRule rule : watchlist.getRules()) {
 *     getLogger().info(rule.getLabel() + " — threshold: " + rule.getThresholdCount());
 * }
 *
 * // Check if an item fingerprint is on the watchlist
 * Optional<DWRule> rule = watchlist.getRule("abc123fingerprint");
 * }</pre>
 */
public interface WatchlistAPI {

    /**
     * Get all watchlist rules currently loaded.
     *
     * @return unmodifiable list of all rules
     */
    List<DWRule> getRules();

    /**
     * Look up a specific rule by its item fingerprint.
     *
     * @param fingerprint the item fingerprint (unique key)
     * @return an {@link Optional} containing the rule if found, or empty
     */
    Optional<DWRule> getRule(String fingerprint);

    /**
     * Add a new rule to the watchlist.
     * <p>The rule will be persisted to the database and take effect immediately.
     *
     * @param rule the rule to add (fingerprint must be unique)
     * @return {@code true} if the rule was added successfully
     */
    boolean addRule(DWRule rule);

    /**
     * Update an existing rule.
     * <p>The rule is identified by its {@link DWRule#getFingerprint()}.
     *
     * @param rule the updated rule
     * @return {@code true} if the rule was found and updated
     */
    boolean updateRule(DWRule rule);

    /**
     * Remove a rule from the watchlist.
     * <p>This permanently removes the rule from the database.
     *
     * @param fingerprint the fingerprint of the rule to remove
     * @return {@code true} if the rule was found and removed
     */
    boolean removeRule(String fingerprint);

    /**
     * Get the total number of watchlist rules.
     *
     * @return rule count
     */
    int getRuleCount();
}
