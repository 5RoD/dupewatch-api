package dev.gravemc.dupewatch.api.model;

import java.util.Collections;
import java.util.List;

/**
 * Public data object representing a DupeWatch watchlist rule.
 *
 * <p>This is a clean, Bukkit-free DTO intended for use by third-party plugins.
 *
 * <p>Obtain instances via {@link dev.gravemc.dupewatch.api.WatchlistAPI}.
 */
public final class DWRule {

    private final String fingerprint;
    private final String label;
    private final int thresholdCount;
    private final int windowMinutes;
    private final int maxAmount;
    private final String scope;
    private final String severity;
    private final List<String> actions;

    private DWRule(Builder builder) {
        this.fingerprint = builder.fingerprint;
        this.label = builder.label;
        this.thresholdCount = builder.thresholdCount;
        this.windowMinutes = builder.windowMinutes;
        this.maxAmount = builder.maxAmount;
        this.scope = builder.scope;
        this.severity = builder.severity;
        this.actions = builder.actions != null ? Collections.unmodifiableList(builder.actions) : Collections.emptyList();
    }

    /**
     * The item fingerprint that uniquely identifies this rule.
     * Used as the primary key for rule lookup.
     */
    public String getFingerprint() { return fingerprint; }

    /** Human-readable label for this rule (e.g., "Diamond Sword Dupe"). */
    public String getLabel() { return label; }

    /**
     * Number of matching items required to trigger this rule.
     * For example, {@code 10} means the rule fires when 10+ matching items are found.
     */
    public int getThresholdCount() { return thresholdCount; }

    /**
     * Rolling time window in minutes for the threshold count.
     * For example, {@code 1440} = 24-hour window.
     */
    public int getWindowMinutes() { return windowMinutes; }

    /**
     * Maximum total item amount before triggering (absolute cap).
     * {@code 0} means disabled.
     */
    public int getMaxAmount() { return maxAmount; }

    /**
     * Scan scope. One of:
     * <ul>
     *   <li>{@code "inventory"} — only main inventory</li>
     *   <li>{@code "inventory+ender"} — main inventory + ender chest</li>
     *   <li>{@code "everywhere"} — all reachable containers</li>
     * </ul>
     */
    public String getScope() { return scope; }

    /**
     * Severity level: {@code "low"}, {@code "med"}, or {@code "high"}.
     */
    public String getSeverity() { return severity; }

    /**
     * List of actions fired when this rule triggers.
     * Possible values: {@code "ALERT_STAFF"}, {@code "LOG_CASE"},
     * {@code "DISCORD_WEBHOOK"}, {@code "RUN_COMMANDS"}, {@code "CONFISCATE_OPTIONAL"}.
     */
    public List<String> getActions() { return actions; }

    /** Returns {@code true} if this rule has the given action. */
    public boolean hasAction(String action) {
        return actions.contains(action);
    }

    @Override
    public String toString() {
        return "DWRule{fingerprint='" + fingerprint + "', label='" + label +
                "', threshold=" + thresholdCount + ", severity='" + severity + "'}";
    }

    /** Builder for constructing {@link DWRule} instances. */
    public static final class Builder {
        private String fingerprint;
        private String label;
        private int thresholdCount = 10;
        private int windowMinutes = 1440;
        private int maxAmount = 256;
        private String scope = "inventory+ender";
        private String severity = "med";
        private List<String> actions;

        public Builder fingerprint(String fingerprint) { this.fingerprint = fingerprint; return this; }
        public Builder label(String label) { this.label = label; return this; }
        public Builder thresholdCount(int thresholdCount) { this.thresholdCount = thresholdCount; return this; }
        public Builder windowMinutes(int windowMinutes) { this.windowMinutes = windowMinutes; return this; }
        public Builder maxAmount(int maxAmount) { this.maxAmount = maxAmount; return this; }
        public Builder scope(String scope) { this.scope = scope; return this; }
        public Builder severity(String severity) { this.severity = severity; return this; }
        public Builder actions(List<String> actions) { this.actions = actions; return this; }

        public DWRule build() { return new DWRule(this); }
    }
}
