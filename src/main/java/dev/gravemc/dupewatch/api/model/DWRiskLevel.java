package dev.gravemc.dupewatch.api.model;

/**
 * Represents a player's DupeWatch risk level, derived from their cumulative ML risk score.
 *
 * <p>Risk levels are used to quickly communicate how suspicious a player is based on
 * their detection history. Scores decay over time.
 *
 * <p>Obtain a player's risk level via {@link dev.gravemc.dupewatch.api.PlayerRiskAPI#getRiskLevel(java.util.UUID)}.
 */
public enum DWRiskLevel {

    /** Risk score below 0.5 — player has little or no detection history. */
    MINIMAL("Minimal", "§7", 0.0),

    /** Risk score 0.5–1.5 — player has had minor flags. */
    LOW("Low", "§a", 0.5),

    /** Risk score 1.5–3.0 — player has had notable flags worth monitoring. */
    MODERATE("Moderate", "§e", 1.5),

    /** Risk score 3.0–5.0 — player has a significant detection history. */
    HIGH("High", "§c", 3.0),

    /** Risk score 5.0+ — player has an extensive detection history. Immediate action recommended. */
    EXTREME("Extreme", "§c§l", 5.0);

    private final String displayName;
    private final String colorCode;
    private final double minScore;

    DWRiskLevel(String displayName, String colorCode, double minScore) {
        this.displayName = displayName;
        this.colorCode = colorCode;
        this.minScore = minScore;
    }

    /** Human-readable display name (e.g., {@code "Moderate"}). */
    public String getDisplayName() { return displayName; }

    /**
     * Legacy Minecraft color code prefix for in-game display (e.g., {@code "§e"}).
     * Use with adventure/minimessage if available on your server.
     */
    public String getColorCode() { return colorCode; }

    /** Minimum score required to be at this risk level. */
    public double getMinScore() { return minScore; }

    /**
     * Determine the {@link DWRiskLevel} for a given numeric risk score.
     *
     * @param score the risk score (typically 0.0 and above)
     * @return the corresponding risk level
     */
    public static DWRiskLevel fromScore(double score) {
        if (score >= 5.0) return EXTREME;
        if (score >= 3.0) return HIGH;
        if (score >= 1.5) return MODERATE;
        if (score >= 0.5) return LOW;
        return MINIMAL;
    }

    @Override
    public String toString() { return displayName; }
}
