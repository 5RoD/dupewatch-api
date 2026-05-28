package dev.gravemc.dupewatch.api.impl;

import dev.gravemc.dupewatch.DupeWatchPlugin;
import dev.gravemc.dupewatch.api.PlayerRiskAPI;
import dev.gravemc.dupewatch.api.model.DWRiskLevel;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Internal implementation of {@link PlayerRiskAPI}.
 * Delegates to the internal {@link PlayerRiskService}.
 */
public class PlayerRiskAPIImpl implements PlayerRiskAPI {

    private final DupeWatchPlugin plugin;

    public PlayerRiskAPIImpl(DupeWatchPlugin plugin) {
        this.plugin = plugin;
    }

    private dev.gravemc.dupewatch.ml.risk.PlayerRiskService getRiskService() {
        var mlService = plugin.getMLService();
        return mlService != null ? mlService.getPlayerRiskService() : null;
    }

    @Override
    public CompletableFuture<Double> getRiskScore(UUID playerUuid) {
        var rs = getRiskService();
        if (rs == null) {
            return CompletableFuture.completedFuture(0.0);
        }
        return rs.getRiskScore(playerUuid);
    }

    @Override
    public CompletableFuture<DWRiskLevel> getRiskLevel(UUID playerUuid) {
        return getRiskScore(playerUuid).thenApply(DWRiskLevel::fromScore);
    }

    @Override
    public boolean isPlayerFlagged(UUID playerUuid) {
        return plugin.getCaseManager() != null
                && plugin.getCaseManager().getActiveCaseCount() > 0
                && !plugin.getCaseManager().getCasesForPlayer(playerUuid).isEmpty()
                && plugin.getCaseManager().getCasesForPlayer(playerUuid)
                        .stream().anyMatch(c -> !c.isResolved());
    }

    @Override
    public CompletableFuture<String> getFormattedRiskScore(UUID playerUuid) {
        return getRiskScore(playerUuid).thenApply(score -> {
            DWRiskLevel level = DWRiskLevel.fromScore(score);
            return level.getColorCode() + String.format("%.2f", score) + " §7(" + level.getDisplayName() + ")";
        });
    }
}
