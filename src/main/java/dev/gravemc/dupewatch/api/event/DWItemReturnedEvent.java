package dev.gravemc.dupewatch.api.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

import dev.gravemc.dupewatch.api.model.DWQuarantinedItem;

/**
 * Fired by DupeWatch when a quarantined item is returned to a player.
 *
 * <p>This event fires both for immediate deliveries (online player) and
 * queued deliveries (offline player — fires when item is queued, not when delivered).
 *
 * <h2>Example</h2>
 * <pre>{@code
 * @EventHandler
 * public void onItemReturned(DWItemReturnedEvent event) {
 *     getLogger().info(event.getReturnedBy() + " returned item " + event.getItem().getQuarantineId()
 *         + " to player UUID " + event.getItem().getPlayerUuid());
 * }
 * }</pre>
 */
public class DWItemReturnedEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final DWQuarantinedItem item;
    private final String returnedBy;

    public DWItemReturnedEvent(DWQuarantinedItem item, String returnedBy) {
        this.item = item;
        this.returnedBy = returnedBy;
    }

    /**
     * Get the returned item information.
     *
     * @return the returned item
     */
    public DWQuarantinedItem getItem() { return item; }

    /**
     * Get the name of the staff member who authorized the return.
     *
     * @return staff name
     */
    public String getReturnedBy() { return returnedBy; }

    @Override
    public HandlerList getHandlers() { return HANDLERS; }

    public static HandlerList getHandlerList() { return HANDLERS; }
}
