package dev.gravemc.dupewatch.api.event;

import dev.gravemc.dupewatch.api.model.DWCase;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Fired by DupeWatch when an investigation case is resolved.
 *
 * <p>This event fires <em>after</em> the case has been marked resolved in the database.
 * It is informational and cannot be cancelled.
 *
 * <h2>Example</h2>
 * <pre>{@code
 * @EventHandler
 * public void onCaseResolved(DWCaseResolvedEvent event) {
 *     getLogger().info(event.getResolvedBy() + " resolved case " + event.getCase().getCaseId());
 * }
 * }</pre>
 */
public class DWCaseResolvedEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final DWCase dwCase;
    private final String resolvedBy;

    public DWCaseResolvedEvent(DWCase dwCase, String resolvedBy) {
        this.dwCase = dwCase;
        this.resolvedBy = resolvedBy;
    }

    /**
     * Get the case that was resolved.
     *
     * @return the resolved case
     */
    public DWCase getCase() { return dwCase; }

    /**
     * Get the name of the staff member (or system) that resolved the case.
     *
     * @return resolver name (never {@code null})
     */
    public String getResolvedBy() { return resolvedBy; }

    @Override
    public HandlerList getHandlers() { return HANDLERS; }

    public static HandlerList getHandlerList() { return HANDLERS; }
}
