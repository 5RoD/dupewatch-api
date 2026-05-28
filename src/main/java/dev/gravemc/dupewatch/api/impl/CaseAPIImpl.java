package dev.gravemc.dupewatch.api.impl;

import dev.gravemc.dupewatch.DupeWatchPlugin;
import dev.gravemc.dupewatch.api.CaseAPI;
import dev.gravemc.dupewatch.api.model.DWCase;
import dev.gravemc.dupewatch.manager.CaseManager;
import dev.gravemc.dupewatch.model.Case;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Internal implementation of {@link CaseAPI}.
 * Delegates to the internal {@link CaseManager}.
 */
public class CaseAPIImpl implements CaseAPI {

    private final DupeWatchPlugin plugin;

    public CaseAPIImpl(DupeWatchPlugin plugin) {
        this.plugin = plugin;
    }

    private CaseManager getCaseManager() {
        return plugin.getCaseManager();
    }

    /** Convert internal Case model to public DWCase DTO. */
    private DWCase toDTO(Case c) {
        return new DWCase.Builder()
                .caseId(c.getCaseId())
                .playerUuid(c.getPlayerUuid())
                .playerName(c.getPlayerName())
                .timestamp(c.getTimestamp())
                .ruleLabel(c.getRuleLabel())
                .fingerprint(c.getFingerprint())
                .severity(c.getSeverity())
                .resolved(c.isResolved())
                .resolvedBy(c.getResolvedBy())
                .resolution(c.getResolution())
                .notes(c.getNotes())
                .itemCount(c.getItemCount())
                .build();
    }

    @Override
    public List<DWCase> getActiveCases() {
        return getCaseManager().getAllActiveCases()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<DWCase> getResolvedCases() {
        return getCaseManager().getAllResolvedCases()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<DWCase> getCasesForPlayer(UUID playerUuid) {
        return getCaseManager().getCasesByPlayer(playerUuid, Integer.MAX_VALUE)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<DWCase> getCase(String caseId) {
        return getCaseManager().getCase(caseId).map(this::toDTO);
    }

    @Override
    public boolean resolveCase(String caseId, String resolvedBy, String resolution) {
        return getCaseManager().resolveCase(caseId, resolvedBy, resolution);
    }

    @Override
    public boolean unresolveCase(String caseId) {
        try {
            getCaseManager().unresolveCase(caseId);
            return true;
        } catch (Exception e) {
            plugin.getLogger().warning("[DupeWatchAPI] Failed to unresolve case " + caseId + ": " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deleteCase(String caseId) {
        return getCaseManager().deleteCase(caseId);
    }

    @Override
    public boolean addNote(String caseId, String note) {
        try {
            Optional<Case> caseOpt = getCaseManager().getCase(caseId);
            if (caseOpt.isEmpty()) return false;
            Case c = caseOpt.get();
            c.addNote(note);
            return getCaseManager().updateCase(c);
        } catch (Exception e) {
            plugin.getLogger().warning("[DupeWatchAPI] Failed to add note to case " + caseId + ": " + e.getMessage());
            return false;
        }
    }

    @Override
    public long getActiveCaseCount() {
        return getCaseManager().getActiveCaseCount();
    }

    @Override
    public long getResolvedCaseCount() {
        return getCaseManager().getResolvedCaseCount();
    }
}
