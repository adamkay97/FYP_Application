package Classes;

public class ScoringBound 
{
    private final int lowerBound;
    private final int upperBound;
    private final int riskLevelID;
    
    public ScoringBound(int lb, int ub, int riskId)
    {
        lowerBound = lb;
        upperBound = ub;
        riskLevelID = riskId;
    }
    
    public int getLowerBound() { return lowerBound; }
    public int getUpperBound() { return upperBound; }
    public int getRiskLevelID() { return riskLevelID; }
}
