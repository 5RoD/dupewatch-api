package dev.gravemc.dupewatch.api.model;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Public data object representing the result of a DupeWatch player scan.
 *
 * <p>Returned by {@link dev.gravemc.dupewatch.api.ScanAPI#scanPlayer(UUID)}.
 */
public final class DWScanResult {

    private final UUID playerUuid;
    private final String playerName;
    private final long scanTimestamp;
    private final boolean triggered;
    private final List<String> flaggedRuleLabels;
    private final List<String> flaggedFingerprints;
    private final int totalFlaggedItems;

    private DWScanResult(Builder builder) {
        this.playerUuid = builder.playerUuid;
        this.playerName = builder.playerName;
        this.scanTimestamp = builder.scanTimestamp;
        this.triggered = builder.triggered;
        this.flaggedRuleLabels = builder.flaggedRuleLabels != null
                ? Collections.unmodifiableList(builder.flaggedRuleLabels)
                : Collections.emptyList();
        this.flaggedFingerprints = builder.flaggedFingerprints != null
                ? Collections.unmodifiableList(builder.flaggedFingerprints)
                : Collections.emptyList();
        this.totalFlaggedItems = builder.totalFlaggedItems;
    }

    /** UUID of the scanned player. */
    public UUID getPlayerUuid() { return playerUuid; }

    /** In-game name of the scanned player. */
    public String getPlayerName() { return playerName; }

    /** Unix epoch milliseconds when the scan was performed. */
    public long getScanTimestamp() { return scanTimestamp; }

    /** Returns {@code true} if at least one watch rule was triggered by this scan. */
    public boolean isTriggered() { return triggered; }

    /** Human-readable labels of all rules that were triggered. */
    public List<String> getFlaggedRuleLabels() { return flaggedRuleLabels; }

    /** Item fingerprints that were flagged during this scan. */
    public List<String> getFlaggedFingerprints() { return flaggedFingerprints; }

    /** Total number of flagged items found across all triggered rules. */
    public int getTotalFlaggedItems() { return totalFlaggedItems; }

    @Override
    public String toString() {
        return "DWScanResult{player='" + playerName + "', triggered=" + triggered +
                ", rules=" + flaggedRuleLabels.size() + ", items=" + totalFlaggedItems + "}";
    }

    /** Builder for constructing {@link DWScanResult} instances. Used internally by DupeWatch. */
    public static final class Builder {
        private UUID playerUuid;
        private String playerName;
        private long scanTimestamp = System.currentTimeMillis();
        private boolean triggered;
        private List<String> flaggedRuleLabels;
        private List<String> flaggedFingerprints;
        private int totalFlaggedItems;

        public Builder playerUuid(UUID playerUuid) { this.playerUuid = playerUuid; return this; }
        public Builder playerName(String playerName) { this.playerName = playerName; return this; }
        public Builder scanTimestamp(long scanTimestamp) { this.scanTimestamp = scanTimestamp; return this; }
        public Builder triggered(boolean triggered) { this.triggered = triggered; return this; }
        public Builder flaggedRuleLabels(List<String> flaggedRuleLabels) { this.flaggedRuleLabels = flaggedRuleLabels; return this; }
        public Builder flaggedFingerprints(List<String> flaggedFingerprints) { this.flaggedFingerprints = flaggedFingerprints; return this; }
        public Builder totalFlaggedItems(int totalFlaggedItems) { this.totalFlaggedItems = totalFlaggedItems; return this; }

        public DWScanResult build() { return new DWScanResult(this); }
    }
}
