package dev.gravemc.dupewatch.api.event;

import dev.gravemc.dupewatch.api.model.DWScanResult;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Fired by DupeWatch when a player scan completes.
 *
 * <p>This event fires after every completed scan, regardless of whether any rules
 * were triggered. Check {@link DWScanResult#isTriggered()} to see if the scan
 * resulted in a flag.
 *
 * <h2>Example</h2>
 * <pre>{@code
 * @EventHandler
 * public void onScanComplete(DWScanCompleteEvent event) {
 *     if (event.getScanner() != null) {
 *         event.getScanner().sendMessage("Scan of " + event.getResult().getPlayerName()
 *             + " complete. Triggered: " + event.getResult().isTriggered());
 *     }
 * }
 * }</pre>
 */
public class DWScanCompleteEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final DWScanResult result;
    private final Player scanner; // nullable — null for automated scans

    public DWScanCompleteEvent(DWScanResult result, Player scanner) {
        this.result = result;
        this.scanner = scanner;
    }

    /**
     * Get the result of the completed scan.
     *
     * @return the scan result (never {@code null})
     */
    public DWScanResult getResult() { return result; }

    /**
     * Get the staff member who triggered the scan, or {@code null} for automated scans.
     *
     * @return the staff player, or {@code null}
     */
    public Player getScanner() { return scanner; }

    /** Returns {@code true} if this was triggered by a staff member manually. */
    public boolean isManualScan() { return scanner != null; }

    @Override
    public HandlerList getHandlers() { return HANDLERS; }

    public static HandlerList getHandlerList() { return HANDLERS; }
}
