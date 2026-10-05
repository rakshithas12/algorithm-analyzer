package com.algorithm.algorithm;

/** Describes theoretical complexity; it contains no measured runtime data. */
public final class ComplexityInfo {
    private final String bestCaseTime;
    private final String averageCaseTime;
    private final String worstCaseTime;
    private final String auxiliarySpace;
    private final String notes;

    public ComplexityInfo(String bestCaseTime, String averageCaseTime, String worstCaseTime,
            String auxiliarySpace, String notes) {
        this.bestCaseTime = bestCaseTime;
        this.averageCaseTime = averageCaseTime;
        this.worstCaseTime = worstCaseTime;
        this.auxiliarySpace = auxiliarySpace;
        this.notes = notes;
    }

    public String getBestCaseTime() { return bestCaseTime; }
    public String getAverageCaseTime() { return averageCaseTime; }
    public String getWorstCaseTime() { return worstCaseTime; }
    public String getAuxiliarySpace() { return auxiliarySpace; }
    public String getNotes() { return notes; }

    public String getSummary() {
        return "Best " + bestCaseTime + " · Average " + averageCaseTime + " · Worst "
                + worstCaseTime + " · Space " + auxiliarySpace;
    }
}
