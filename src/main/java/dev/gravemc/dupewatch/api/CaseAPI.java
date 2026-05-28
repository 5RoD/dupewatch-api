package dev.gravemc.dupewatch.api;

import dev.gravemc.dupewatch.api.model.DWCase;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * API for querying and managing DupeWatch investigation cases.
 *
 * <p>Obtain an instance via {@link DupeWatchAPI#cases()}.
 *
 * <h2>Example</h2>
 * <pre>{@code
 * CaseAPI cases = DupeWatchAPI.get().cases();
 *
 * // Get all active cases for a player
 * List<DWCase> playerCases = cases.getCasesForPlayer(player.getUniqueId());
 *
 * // Resolve a case
 * cases.resolveCase("01HX...", "AdminName", "Confirmed dupe — items removed");
 * }</pre>
 */
public interface CaseAPI {

    /**
     * Get all currently active (unresolved) cases.
     *
     * @return unmodifiable list of active cases, newest first
     */
    List<DWCase> getActiveCases();

    /**
     * Get all resolved cases.
     *
     * @return unmodifiable list of resolved cases, newest first
     */
    List<DWCase> getResolvedCases();

    /**
     * Get all cases (active and resolved) associated with a specific player.
     *
     * @param playerUuid the player's UUID
     * @return list of cases for the player, newest first
     */
    List<DWCase> getCasesForPlayer(UUID playerUuid);

    /**
     * Look up a specific case by its ID.
     *
     * @param caseId the case ID (ULID format)
     * @return an {@link Optional} containing the case if found, or empty
     */
    Optional<DWCase> getCase(String caseId);

    /**
     * Resolve an active case.
     *
     * @param caseId     the case ID to resolve
     * @param resolvedBy the name of the staff member resolving the case
     * @param resolution optional resolution notes (may be {@code null})
     * @return {@code true} if the case was found and resolved successfully
     */
    boolean resolveCase(String caseId, String resolvedBy, String resolution);

    /**
     * Re-open a previously resolved case.
     *
     * @param caseId the case ID to un-resolve
     * @return {@code true} if the case was found and un-resolved successfully
     */
    boolean unresolveCase(String caseId);

    /**
     * Permanently delete a case.
     * <p><strong>Warning:</strong> This is irreversible. Prefer resolving cases instead.
     *
     * @param caseId the case ID to delete
     * @return {@code true} if the case was found and deleted
     */
    boolean deleteCase(String caseId);

    /**
     * Add a staff note to an existing case.
     *
     * @param caseId the case ID
     * @param note   the note text (prefix with {@code "[STAFF] "} to mark it as a staff note)
     * @return {@code true} if the note was added successfully
     */
    boolean addNote(String caseId, String note);

    /**
     * Get the current number of active (unresolved) cases.
     *
     * @return active case count
     */
    long getActiveCaseCount();

    /**
     * Get the current number of resolved cases.
     *
     * @return resolved case count
     */
    long getResolvedCaseCount();
}
