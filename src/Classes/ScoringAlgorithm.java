package Classes;

public class ScoringAlgorithm 
{
    private ScoringBound lowRisk;
    private ScoringBound medRisk;
    private ScoringBound highRisk;

    public ScoringAlgorithm() {}
    
    public ScoringBound getLowRisk() { return lowRisk; }
    public ScoringBound getMedRisk() { return medRisk; }
    public ScoringBound getHighRisk() { return highRisk; }
    
    public void setLowRisk(ScoringBound lr) { lowRisk = lr; }
    public void setMedRisk(ScoringBound mr) { medRisk = mr; }
    public void setHighRisk(ScoringBound hr) { highRisk = hr; }
}
