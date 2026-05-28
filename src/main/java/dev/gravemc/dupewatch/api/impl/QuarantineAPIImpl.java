package dev.gravemc.dupewatch.api.impl;

import dev.gravemc.dupewatch.DupeWatchPlugin;
import dev.gravemc.dupewatch.api.QuarantineAPI;
import dev.gravemc.dupewatch.api.model.DWQuarantinedItem;
import dev.gravemc.dupewatch.model.QuarantinedItem;
import dev.gravemc.dupewatch.service.QuarantineService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Internal implementation of {@link QuarantineAPI}.
 * Delegates to the internal {@link QuarantineService}.
 */
public class QuarantineAPIImpl implements QuarantineAPI {

    private final DupeWatchPlugin plugin;

    public QuarantineAPIImpl(DupeWatchPlugin plugin) {
        this.plugin = plugin;
    }

    private QuarantineService getQuarantineService() {
        return plugin.getQuarantineService();
    }

    /** Convert internal QuarantinedItem to public DWQuarantinedItem DTO. */
    private DWQuarantinedItem toDTO(QuarantinedItem qi) {
        return new DWQuarantinedItem.Builder()
                .quarantineId(qi.getQuarantineId())
                .playerUuid(qi.getPlayerUuid())
                .playerName(qi.getPlayerName())
                .reason(qi.getReason())
                .timestamp(qi.getTimestamp())
                .status(qi.getStatus())
                .handledBy(qi.getHandledBy())
                .pendingReturn(qi.isPendingReturn())
                .build();
    }

    @Override
    public boolean isAvailable() {
        QuarantineService qs = getQuarantineService();
        return qs != null && qs.isAvailable() && qs.isQuarantineMode();
    }

    @Override
    public CompletableFuture<List<DWQuarantinedItem>> getQuarantinedItems(UUID playerUuid) {
        QuarantineService qs = getQuarantineService();
        if (qs == null || !qs.isAvailable()) {
            return CompletableFuture.completedFuture(new ArrayList<>());
        }
        return qs.getAllQuarantined().thenApply(items ->
                items.stream()
                        .filter(qi -> playerUuid.equals(qi.getPlayerUuid()))
                        .map(this::toDTO)
                        .collect(Collectors.toList()));
    }

    @Override
    public CompletableFuture<List<DWQuarantinedItem>> getAllQuarantinedItems() {
        QuarantineService qs = getQuarantineService();
        if (qs == null || !qs.isAvailable()) {
            return CompletableFuture.completedFuture(new ArrayList<>());
        }
        return qs.getAllQuarantined().thenApply(items ->
                items.stream().map(this::toDTO).collect(Collectors.toList()));
    }

    @Override
    public CompletableFuture<Boolean> returnItem(String quarantineId, String staffName) {
        QuarantineService qs = getQuarantineService();
        if (qs == null || !qs.isAvailable()) {
            return CompletableFuture.completedFuture(false);
        }
        return qs.returnItem(quarantineId, staffName);
    }

    @Override
    public CompletableFuture<Boolean> deleteItem(String quarantineId, String staffName) {
        QuarantineService qs = getQuarantineService();
        if (qs == null || !qs.isAvailable()) {
            return CompletableFuture.completedFuture(false);
        }
        return qs.deleteItem(quarantineId, staffName);
    }

    @Override
    public CompletableFuture<Integer> returnAllForPlayer(UUID playerUuid, String staffName) {
        QuarantineService qs = getQuarantineService();
        if (qs == null || !qs.isAvailable()) {
            return CompletableFuture.completedFuture(0);
        }
        return qs.returnAllForPlayer(playerUuid, staffName);
    }

    @Override
    public CompletableFuture<Long> countQuarantined() {
        QuarantineService qs = getQuarantineService();
        if (qs == null || !qs.isAvailable()) {
            return CompletableFuture.completedFuture(0L);
        }
        return qs.countQuarantined();
    }
}
