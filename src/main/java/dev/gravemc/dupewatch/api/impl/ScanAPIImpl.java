package dev.gravemc.dupewatch.api.impl;

import dev.gravemc.dupewatch.DupeWatchPlugin;
import dev.gravemc.dupewatch.api.ScanAPI;
import dev.gravemc.dupewatch.api.event.DWScanCompleteEvent;
import dev.gravemc.dupewatch.api.model.DWScanResult;
import dev.gravemc.dupewatch.model.ScanResult;
import dev.gravemc.dupewatch.service.ScanService;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Internal implementation of {@link ScanAPI}.
 * Delegates to the internal {@link ScanService}.
 */
public class ScanAPIImpl implements ScanAPI {

    private final DupeWatchPlugin plugin;

    public ScanAPIImpl(DupeWatchPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public CompletableFuture<DWScanResult> scanPlayer(UUID playerUuid) {
        Player player = Bukkit.getPlayer(playerUuid);

        // Build a non-triggered result for offline or unavailable player
        if (player == null || !player.isOnline()) {
            return CompletableFuture.completedFuture(new DWScanResult.Builder()
                    .playerUuid(playerUuid)
                    .playerName(playerUuid.toString())
                    .triggered(false)
                    .flaggedRuleLabels(new ArrayList<>())
                    .flaggedFingerprints(new ArrayList<>())
                    .totalFlaggedItems(0)
                    .build());
        }

        ScanService scanService = plugin.getScanService();
        if (scanService == null || !isScanningEnabled()) {
            return CompletableFuture.completedFuture(new DWScanResult.Builder()
                    .playerUuid(playerUuid)
                    .playerName(player.getName())
                    .triggered(false)
                    .flaggedRuleLabels(new ArrayList<>())
                    .flaggedFingerprints(new ArrayList<>())
                    .totalFlaggedItems(0)
                    .build());
        }

        final Player finalPlayer = player;
        return CompletableFuture.supplyAsync(() -> {
            List<String> ruleLabels = new ArrayList<>();
            List<String> fingerprints = new ArrayList<>();
            int[] totalItems = {0};
            boolean[] triggered = {false};

            try {
                ScanResult internalResult = scanService.scanPlayerSync(finalPlayer);

                if (internalResult != null && internalResult.getFingerprintCounts() != null) {
                    for (java.util.Map.Entry<String, Integer> entry : internalResult.getFingerprintCounts().entrySet()) {
                        String fp = entry.getKey();
                        int count = entry.getValue();
                        // Only count fingerprints that are on the watchlist
                        plugin.getWatchlistManager().getRule(fp).ifPresent(rule -> {
                            triggered[0] = true;
                            ruleLabels.add(rule.getLabel());
                            fingerprints.add(fp);
                        });
                        totalItems[0] += count;
                    }
                }
            } catch (Exception e) {
                plugin.getLogger().warning("[DupeWatchAPI] Error during API scan of " + finalPlayer.getName() + ": " + e.getMessage());
            }

            DWScanResult apiResult = new DWScanResult.Builder()
                    .playerUuid(playerUuid)
                    .playerName(finalPlayer.getName())
                    .triggered(triggered[0])
                    .flaggedRuleLabels(ruleLabels)
                    .flaggedFingerprints(fingerprints)
                    .totalFlaggedItems(totalItems[0])
                    .build();

            // Fire the scan complete event on the main thread
            Bukkit.getScheduler().runTask(plugin, () -> {
                DWScanCompleteEvent event = new DWScanCompleteEvent(apiResult, null);
                Bukkit.getPluginManager().callEvent(event);
            });

            return apiResult;
        });
    }

    @Override
    public boolean isScanningEnabled() {
        return plugin.getConfigManager() != null
                && plugin.getConfigManager().getScanOnlineIntervalSeconds() > 0;
    }

    @Override
    public int getMaxConcurrentScans() {
        return plugin.getConfigManager() != null
                ? plugin.getConfigManager().getMaxConcurrentScans()
                : 5;
    }
}
