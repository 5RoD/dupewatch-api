package dev.gravemc.dupewatch.api.impl;

import dev.gravemc.dupewatch.DupeWatchPlugin;
import dev.gravemc.dupewatch.api.MoneyWatchAPI;
import dev.gravemc.dupewatch.service.MoneyWatchService;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Internal implementation of {@link MoneyWatchAPI}.
 * Delegates to the internal {@link MoneyWatchService}.
 */
public class MoneyWatchAPIImpl implements MoneyWatchAPI {

    private final DupeWatchPlugin plugin;

    public MoneyWatchAPIImpl(DupeWatchPlugin plugin) {
        this.plugin = plugin;
    }

    private MoneyWatchService getMoneyWatchService() {
        return plugin.getMoneyWatchService();
    }

    @Override
    public boolean isEnabled() {
        MoneyWatchService mw = getMoneyWatchService();
        return mw != null && mw.isRunning();
    }

    @Override
    public CompletableFuture<Boolean> hasRecentAnomalies(UUID playerUuid) {
        MoneyWatchService mw = getMoneyWatchService();
        if (mw == null || !mw.isRunning()) {
            return CompletableFuture.completedFuture(false);
        }
        return CompletableFuture.supplyAsync(() -> {
            try {
                // getDirtyFlag returns non-null if there is a flagged money anomaly
                return mw.getDirtyFlag(playerUuid) != null;
            } catch (Exception e) {
                plugin.getLogger().warning("[DupeWatchAPI] Error checking money anomalies for " + playerUuid + ": " + e.getMessage());
                return false;
            }
        });
    }

    @Override
    public CompletableFuture<Double> getLastRecordedBalance(UUID playerUuid) {
        MoneyWatchService mw = getMoneyWatchService();
        if (mw == null || !mw.isRunning()) {
            return CompletableFuture.completedFuture(-1.0);
        }
        return CompletableFuture.supplyAsync(() -> {
            try {
                var history = mw.getGainHistory(playerUuid);
                if (history == null || history.isEmpty()) return -1.0;
                // Return the latest recorded "after" balance from the most recent gain record
                var last = history.get(history.size() - 1);
                return last.getBalanceAfter();
            } catch (Exception e) {
                plugin.getLogger().warning("[DupeWatchAPI] Error getting last balance for " + playerUuid + ": " + e.getMessage());
                return -1.0;
            }
        });
    }
}
