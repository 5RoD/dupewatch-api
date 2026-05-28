package dev.gravemc.dupewatch.api.impl;

import dev.gravemc.dupewatch.DupeWatchPlugin;
import dev.gravemc.dupewatch.api.WatchlistAPI;
import dev.gravemc.dupewatch.api.model.DWRule;
import dev.gravemc.dupewatch.manager.WatchlistManager;
import dev.gravemc.dupewatch.model.WatchRule;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Internal implementation of {@link WatchlistAPI}.
 * Delegates to the internal {@link WatchlistManager}.
 */
public class WatchlistAPIImpl implements WatchlistAPI {

    private final DupeWatchPlugin plugin;

    public WatchlistAPIImpl(DupeWatchPlugin plugin) {
        this.plugin = plugin;
    }

    private WatchlistManager getWatchlistManager() {
        return plugin.getWatchlistManager();
    }

    /** Convert internal WatchRule to public DWRule DTO. */
    private DWRule toDTO(WatchRule r) {
        return new DWRule.Builder()
                .fingerprint(r.getFingerprint())
                .label(r.getLabel())
                .thresholdCount(r.getThresholdCount())
                .windowMinutes(r.getWindowMinutes())
                .maxAmount(r.getMaxAmount())
                .scope(r.getScope())
                .severity(r.getSeverity())
                .actions(r.getActions() != null ? new ArrayList<>(r.getActions()) : new ArrayList<>())
                .build();
    }

    /** Convert public DWRule DTO to internal WatchRule. */
    private WatchRule fromDTO(DWRule dto) {
        WatchRule rule = new WatchRule(dto.getFingerprint(), dto.getLabel(), dto.getThresholdCount());
        rule.setWindowMinutes(dto.getWindowMinutes());
        rule.setMaxAmount(dto.getMaxAmount());
        rule.setScope(dto.getScope());
        rule.setSeverity(dto.getSeverity());
        if (dto.getActions() != null) {
            rule.setActions(new ArrayList<>(dto.getActions()));
        }
        return rule;
    }

    @Override
    public List<DWRule> getRules() {
        return getWatchlistManager().getAllRules()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<DWRule> getRule(String fingerprint) {
        return getWatchlistManager().getRule(fingerprint).map(this::toDTO);
    }

    @Override
    public boolean addRule(DWRule rule) {
        return getWatchlistManager().addRule(fromDTO(rule));
    }

    @Override
    public boolean updateRule(DWRule rule) {
        return getWatchlistManager().updateRule(fromDTO(rule));
    }

    @Override
    public boolean removeRule(String fingerprint) {
        return getWatchlistManager().removeRule(fingerprint);
    }

    @Override
    public int getRuleCount() {
        return getWatchlistManager().getRuleCount();
    }
}
