package Classes;

public class DiagnosisResult 
{
    private final String questionSetName;
    private final int stageOneScore;
    private final String stageOneRisk;
    private final int stageTwoScore;
    private final String overallScreening;
    
    public DiagnosisResult(String setName, int s1Score, String s1Risk, int s2Score, String result)
    {
        questionSetName = setName;
        stageOneScore = s1Score;
        stageOneRisk = s1Risk;
        stageTwoScore = s2Score;
        overallScreening = result;
    }
    
    public String getQuestionSetName() { return questionSetName; }
    public int getStageOneScore() { return stageOneScore; }
    public String getStageOneRisk() { return stageOneRisk; }
    public int getStageTwoScore() { return stageTwoScore; }
    public String getOverallScreening() { return overallScreening; }
}
