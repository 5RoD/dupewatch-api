package dev.gravemc.dupewatch.api.model;

import java.util.UUID;

/**
 * Public data object representing a quarantined item in DupeWatch.
 *
 * <p>Obtain instances via {@link dev.gravemc.dupewatch.api.QuarantineAPI}.
 */
public final class DWQuarantinedItem {

    private final String quarantineId;
    private final UUID playerUuid;
    private final String playerName;
    private final String reason;
    private final long timestamp;
    private final String status;
    private final String handledBy;
    private final boolean pendingReturn;

    private DWQuarantinedItem(Builder builder) {
        this.quarantineId = builder.quarantineId;
        this.playerUuid = builder.playerUuid;
        this.playerName = builder.playerName;
        this.reason = builder.reason;
        this.timestamp = builder.timestamp;
        this.status = builder.status;
        this.handledBy = builder.handledBy;
        this.pendingReturn = builder.pendingReturn;
    }

    /** Unique quarantine identifier. */
    public String getQuarantineId() { return quarantineId; }

    /** UUID of the player whose item was quarantined. */
    public UUID getPlayerUuid() { return playerUuid; }

    /** In-game name of the player whose item was quarantined. */
    public String getPlayerName() { return playerName; }

    /** Human-readable reason for quarantine (e.g., "Rule: Diamond Sword Dupe | Case: 01HX..."). */
    public String getReason() { return reason; }

    /** Unix epoch milliseconds when the item was quarantined. */
    public long getTimestamp() { return timestamp; }

    /**
     * Current status of the quarantined item.
     * One of: {@code "QUARANTINED"}, {@code "RETURNED"}, {@code "DELETED"}.
     */
    public String getStatus() { return status; }

    /** Name of the staff member who handled this item, or {@code null} if not yet handled. */
    public String getHandledBy() { return handledBy; }

    /** Whether this item is queued for return to an offline player. */
    public boolean isPendingReturn() { return pendingReturn; }

    /** Returns {@code true} if the item is still actively quarantined. */
    public boolean isActive() { return "QUARANTINED".equalsIgnoreCase(status); }

    @Override
    public String toString() {
        return "DWQuarantinedItem{id='" + quarantineId + "', player='" + playerName +
                "', status='" + status + "'}";
    }

    /** Builder for constructing {@link DWQuarantinedItem} instances. Used internally by DupeWatch. */
    public static final class Builder {
        private String quarantineId;
        private UUID playerUuid;
        private String playerName;
        private String reason;
        private long timestamp = System.currentTimeMillis();
        private String status = "QUARANTINED";
        private String handledBy;
        private boolean pendingReturn;

        public Builder quarantineId(String quarantineId) { this.quarantineId = quarantineId; return this; }
        public Builder playerUuid(UUID playerUuid) { this.playerUuid = playerUuid; return this; }
        public Builder playerName(String playerName) { this.playerName = playerName; return this; }
        public Builder reason(String reason) { this.reason = reason; return this; }
        public Builder timestamp(long timestamp) { this.timestamp = timestamp; return this; }
        public Builder status(String status) { this.status = status; return this; }
        public Builder handledBy(String handledBy) { this.handledBy = handledBy; return this; }
        public Builder pendingReturn(boolean pendingReturn) { this.pendingReturn = pendingReturn; return this; }

        public DWQuarantinedItem build() { return new DWQuarantinedItem(this); }
    }
}
