package dev.gravemc.dupewatch.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import dev.gravemc.dupewatch.api.model.DWQuarantinedItem;

/**
 * Fired by DupeWatch when items are quarantined from a player's inventory.
 *
 * <p>This event is informational and cannot be cancelled.
 *
 * <h2>Example</h2>
 * <pre>{@code
 * @EventHandler
 * public void onItemQuarantined(DWItemQuarantinedEvent event) {
 *     getLogger().info(event.getItem().getReason() + " - items quarantined from "
 *         + event.getItem().getPlayerName());
 * }
 * }</pre>
 */
public class DWItemQuarantinedEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final DWQuarantinedItem item;

    public DWItemQuarantinedEvent(DWQuarantinedItem item) {
        this.item = item;
    }

    /**
     * Get the quarantined item information.
     *
     * @return the quarantined item
     */
    public DWQuarantinedItem getItem() { return item; }

    @Override
    public HandlerList getHandlers() { return HANDLERS; }

    public static HandlerList getHandlerList() { return HANDLERS; }
}
