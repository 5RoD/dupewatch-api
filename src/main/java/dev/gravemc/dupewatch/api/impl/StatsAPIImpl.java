package dev.gravemc.dupewatch.api.impl;

import dev.gravemc.dupewatch.DupeWatchPlugin;
import dev.gravemc.dupewatch.api.StatsAPI;

/**
 * Internal implementation of {@link StatsAPI}.
 * Queries the various internal managers and services for statistics.
 */
public class StatsAPIImpl implements StatsAPI {

    private final DupeWatchPlugin plugin;

    public StatsAPIImpl(DupeWatchPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getPluginVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public long getActiveCaseCount() {
        return plugin.getCaseManager() != null
                ? plugin.getCaseManager().getActiveCaseCount()
                : 0L;
    }

    @Override
    public long getResolvedCaseCount() {
        return plugin.getCaseManager() != null
                ? plugin.getCaseManager().getResolvedCaseCount()
                : 0L;
    }

    @Override
    public long getTotalCaseCount() {
        return getActiveCaseCount() + getResolvedCaseCount();
    }

    @Override
    public int getRuleCount() {
        return plugin.getWatchlistManager() != null
                ? plugin.getWatchlistManager().getRuleCount()
                : 0;
    }

    @Override
    public long getQuarantinedItemCount() {
        try {
            if (plugin.getQuarantineService() == null || !plugin.getQuarantineService().isAvailable()) {
                return 0L;
            }
            return plugin.getQuarantineService().countQuarantined().join();
        } catch (Exception e) {
            plugin.getLogger().warning("[DupeWatchAPI] Error getting quarantine count: " + e.getMessage());
            return 0L;
        }
    }

    @Override
    public boolean isReady() {
        return plugin.getCaseManager() != null && plugin.getWatchlistManager() != null;
    }
}
