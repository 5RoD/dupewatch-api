package dev.gravemc.dupewatch.api.event;

import dev.gravemc.dupewatch.api.model.DWCase;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Fired by DupeWatch when a new investigation case is created.
 *
 * <p>This event fires <em>after</em> the case has been saved to the database.
 * It is informational and cannot be cancelled.
 *
 * <h2>Example</h2>
 * <pre>{@code
 * @EventHandler
 * public void onCaseCreated(DWCaseCreatedEvent event) {
 *     DWCase c = event.getCase();
 *     Bukkit.broadcastMessage("§c[Security] New case opened for " + c.getPlayerName());
 * }
 * }</pre>
 */
public class DWCaseCreatedEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final DWCase dwCase;

    public DWCaseCreatedEvent(DWCase dwCase) {
        this.dwCase = dwCase;
    }

    /**
     * Get the newly created case.
     *
     * @return the case object
     */
    public DWCase getCase() { return dwCase; }

    @Override
    public HandlerList getHandlers() { return HANDLERS; }

    public static HandlerList getHandlerList() { return HANDLERS; }
}
