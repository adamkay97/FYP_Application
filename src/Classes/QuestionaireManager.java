package Classes;

import java.util.ArrayList;
import java.util.HashMap;

public class QuestionaireManager 
{
    private static HashMap<Integer, Question> questionMap;
    private static HashMap<Integer, FollowUpFlow> followUpMap;
    private static Question currentQuestion;
    private static FollowUpFlow currentFollowUp;
    private static ArrayList<Integer> flaggedQuestions;
    private static int failedQuestions;
    private static Child currentChild;
    private static boolean followUpCompleted = false;
    
    public static void saveQuestionAnswer(int qNumber, QuestionAnswer qAnswer, String qNotes)
    {
        currentQuestion.setQuestionAnswer(qAnswer);
        currentQuestion.setQuestionNotes(qNotes);
        //questionMap.put(qNumber, currentQuestion);
        
        switch(qAnswer)
        {
            case YES:
                if(qNumber == 2 || qNumber == 5 || qNumber == 12)
                    flaggedQuestions.add(qNumber);
                break;
            case NO:
                if(qNumber != 2 && qNumber != 5 && qNumber != 12)
                    flaggedQuestions.add(qNumber);
                break;
        }       
    }
        
    public static void saveFirstStageScore(boolean finished)
    {
        DatabaseManager dbManager = new DatabaseManager();
        String text = getResultInfo(1);
        String[] result = text.split("\n");
        int score = flaggedQuestions.size();
        
        currentChild.setResultScore(score);
        currentChild.setResultText(result[0]);
        
        if(finished)
        {
            if(dbManager.connect())
            {
                dbManager.updateChildScore(result[0], score, currentChild.getChildId());
                dbManager.disconnect();

                flaggedQuestions = new ArrayList<>();
            }
        }
    }
    
    public static String getResultInfo(int stage)
    {
        DatabaseManager db = new DatabaseManager();
        String riskText = "";
        int riskId;
        
        if(stage == 1)
        {
            int score = flaggedQuestions.size();

            if(score <= 2) riskId = 1;
            else if(score <= 7) riskId = 2;
            else riskId = 3;

            if(db.connect())
            {
                riskText = db.getResultInfo(riskId);
                db.disconnect();
            }
        }
        else
        {
            if(failedQuestions >= 2) riskId = 4;
            else riskId = 5;
            
            if(db.connect())
            {
                riskText = db.getResultInfo(riskId);
                db.disconnect();
            }
        }
        return riskText;
    }
     
    public static int getQuestionaireScore()
    {
        return flaggedQuestions.size();
    }
    
    public static String getQuestionText(int qNumber)
    {
        currentQuestion = questionMap.get(qNumber);
        return currentQuestion.getQuestionText();
    }
    
    public static void setQuestionMap(HashMap<Integer, Question> questions)
    {
        questionMap = questions;
        flaggedQuestions = new ArrayList<>();
    }
    
    public static FollowUpFlow getFollowUpFlow(int fNumber)
    {
        currentFollowUp = followUpMap.get(fNumber);
        return currentFollowUp;
    }
    
    public static void setFollowUpCompleted(boolean completed) { followUpCompleted = completed; }
    public static void setFailedQuestions(int failed) { failedQuestions = failed; }
    public static void setCurrentChild(Child child) { currentChild = child; }
    public static void setFollowUpMap(HashMap<Integer, FollowUpFlow> followup) { followUpMap = followup; }
    
    public static Child getCurrentChild() { return currentChild; }
    public static boolean getFollowUpCompleted() { return followUpCompleted; }
    public static int getFailedQuestions() { return failedQuestions; }
    public static ArrayList<Integer> getFlaggedQuestions() { return flaggedQuestions; }
    
}
