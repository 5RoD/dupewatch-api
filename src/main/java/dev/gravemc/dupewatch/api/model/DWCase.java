package dev.gravemc.dupewatch.api.model;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Public data object representing a DupeWatch investigation case.
 *
 * <p>This is a clean, Bukkit-free DTO intended for use by third-party plugins.
 * It does not expose internal DupeWatch classes.
 *
 * <p>Obtain instances via {@link dev.gravemc.dupewatch.api.CaseAPI}.
 */
public final class DWCase {

    private final String caseId;
    private final UUID playerUuid;
    private final String playerName;
    private final long timestamp;
    private final String ruleLabel;
    private final String fingerprint;
    private final String severity;
    private final boolean resolved;
    private final String resolvedBy;
    private final String resolution;
    private final List<String> notes;
    private final int itemCount;

    private DWCase(Builder builder) {
        this.caseId = builder.caseId;
        this.playerUuid = builder.playerUuid;
        this.playerName = builder.playerName;
        this.timestamp = builder.timestamp;
        this.ruleLabel = builder.ruleLabel;
        this.fingerprint = builder.fingerprint;
        this.severity = builder.severity;
        this.resolved = builder.resolved;
        this.resolvedBy = builder.resolvedBy;
        this.resolution = builder.resolution;
        this.notes = builder.notes != null ? Collections.unmodifiableList(builder.notes) : Collections.emptyList();
        this.itemCount = builder.itemCount;
    }

    /** Unique case identifier (ULID format). */
    public String getCaseId() { return caseId; }

    /** UUID of the flagged player. */
    public UUID getPlayerUuid() { return playerUuid; }

    /** In-game name of the flagged player at the time of the flag. */
    public String getPlayerName() { return playerName; }

    /** Unix epoch milliseconds when the case was created. */
    public long getTimestamp() { return timestamp; }

    /** Human-readable label of the watch rule that triggered this case. */
    public String getRuleLabel() { return ruleLabel; }

    /** Item fingerprint that triggered this case. */
    public String getFingerprint() { return fingerprint; }

    /**
     * Severity level: {@code "low"}, {@code "med"}, or {@code "high"}.
     */
    public String getSeverity() { return severity; }

    /** Whether this case has been marked as resolved. */
    public boolean isResolved() { return resolved; }

    /** Name of the staff member who resolved the case, or {@code null} if unresolved. */
    public String getResolvedBy() { return resolvedBy; }

    /** Resolution notes, or {@code null} if unresolved. */
    public String getResolution() { return resolution; }

    /** Read-only list of staff notes attached to this case. */
    public List<String> getNotes() { return notes; }

    /** Total number of flagged items found during the scan. */
    public int getItemCount() { return itemCount; }

    /** Convenience: returns the case creation time as a Java {@link Instant}. */
    public Instant getCreationTime() { return Instant.ofEpochMilli(timestamp); }

    @Override
    public String toString() {
        return "DWCase{caseId='" + caseId + "', player='" + playerName + "', rule='" + ruleLabel +
                "', severity='" + severity + "', resolved=" + resolved + "}";
    }

    /** Builder for constructing {@link DWCase} instances. Used internally by DupeWatch. */
    public static final class Builder {
        private String caseId;
        private UUID playerUuid;
        private String playerName;
        private long timestamp;
        private String ruleLabel;
        private String fingerprint;
        private String severity = "med";
        private boolean resolved;
        private String resolvedBy;
        private String resolution;
        private List<String> notes;
        private int itemCount;

        public Builder caseId(String caseId) { this.caseId = caseId; return this; }
        public Builder playerUuid(UUID playerUuid) { this.playerUuid = playerUuid; return this; }
        public Builder playerName(String playerName) { this.playerName = playerName; return this; }
        public Builder timestamp(long timestamp) { this.timestamp = timestamp; return this; }
        public Builder ruleLabel(String ruleLabel) { this.ruleLabel = ruleLabel; return this; }
        public Builder fingerprint(String fingerprint) { this.fingerprint = fingerprint; return this; }
        public Builder severity(String severity) { this.severity = severity; return this; }
        public Builder resolved(boolean resolved) { this.resolved = resolved; return this; }
        public Builder resolvedBy(String resolvedBy) { this.resolvedBy = resolvedBy; return this; }
        public Builder resolution(String resolution) { this.resolution = resolution; return this; }
        public Builder notes(List<String> notes) { this.notes = notes; return this; }
        public Builder itemCount(int itemCount) { this.itemCount = itemCount; return this; }

        public DWCase build() { return new DWCase(this); }
    }
}
