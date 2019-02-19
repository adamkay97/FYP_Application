package Classes;

public class DiagnosisResult 
{
    private final int stageOneScore;
    private final String stageOneRisk;
    private final int stageTwoScore;
    private final String overallScreening;
    
    public DiagnosisResult(int s1Score, String s1Risk, int s2Score, String result)
    {
        stageOneScore = s1Score;
        stageOneRisk = s1Risk;
        stageTwoScore = s2Score;
        overallScreening = result;
    }
    
    public int getStageOneScore() { return stageOneScore; }
    public String getStageOneRisk() { return stageOneRisk; }
    public int getStageTwoScore() { return stageTwoScore; }
    public String getOverallScreening() { return overallScreening; }
}
