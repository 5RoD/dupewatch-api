package dev.gravemc.dupewatch.api.event;

import dev.gravemc.dupewatch.api.model.DWRule;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Fired by DupeWatch when a player's inventory scan triggers a watchlist rule.
 *
 * <p>This event fires <strong>before</strong> cases are created and alerts are sent.
 * It is <strong>cancellable</strong> — if cancelled, DupeWatch will fully suppress
 * the detection: no case will be created, no staff alerts sent, no Discord message,
 * and no commands executed.
 *
 * <h2>Example — Suppress alerts for trusted players</h2>
 * <pre>{@code
 * @EventHandler
 * public void onPlayerFlagged(DWPlayerFlaggedEvent event) {
 *     // Suppress DupeWatch for players in a specific group
 *     if (myPermissionPlugin.isInGroup(event.getPlayer(), "trusted")) {
 *         event.setCancelled(true);
 *     }
 * }
 * }</pre>
 *
 * <h2>Example — Custom punishment</h2>
 * <pre>{@code
 * @EventHandler
 * public void onPlayerFlagged(DWPlayerFlaggedEvent event) {
 *     if (event.getRule().getSeverity().equals("high")) {
 *         // Add custom logic alongside DupeWatch's normal handling
 *         myPunishmentPlugin.warn(event.getPlayer(), "Suspected item duplication");
 *     }
 * }
 * }</pre>
 */
public class DWPlayerFlaggedEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    private final DWRule rule;
    private final int itemCount;
    private boolean cancelled;

    public DWPlayerFlaggedEvent(Player player, DWRule rule, int itemCount) {
        this.player = player;
        this.rule = rule;
        this.itemCount = itemCount;
        this.cancelled = false;
    }

    /**
     * Get the player who triggered the watch rule.
     *
     * @return the flagged player
     */
    public Player getPlayer() { return player; }

    /**
     * Get the watch rule that was triggered.
     *
     * @return the triggered rule
     */
    public DWRule getRule() { return rule; }

    /**
     * Get the number of matching items found that triggered the rule.
     *
     * @return item count
     */
    public int getItemCount() { return itemCount; }

    /**
     * If {@code true}, DupeWatch will skip case creation, staff alerts,
     * Discord webhooks, and command execution for this detection.
     */
    @Override
    public boolean isCancelled() { return cancelled; }

    /**
     * Cancel this detection event.
     * <p>Setting this to {@code true} fully suppresses DupeWatch's response
     * to this flag — no case, no alerts, no commands.
     */
    @Override
    public void setCancelled(boolean cancel) { this.cancelled = cancel; }

    @Override
    public HandlerList getHandlers() { return HANDLERS; }

    public static HandlerList getHandlerList() { return HANDLERS; }
}
