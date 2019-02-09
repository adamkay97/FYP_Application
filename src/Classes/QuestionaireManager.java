package Classes;

import Enums.QuestionAnswer;
import java.util.ArrayList;
import java.util.HashMap;

public class QuestionaireManager 
{
    private static HashMap<Integer, Question> questionMap;
    private static HashMap<Integer, FollowUpFlow> followUpMap;
    private static Question currentQuestion;
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
        
    public static void saveFirstStageScore()
    {
        DatabaseManager dbManager = new DatabaseManager();
        String text = getResultInfo(1);
        String[] result = text.split("\n");
        int score = flaggedQuestions.size();
        
        currentChild.setResultScore(score);
        currentChild.setResultText(result[0]);
        
        if(dbManager.connect())
        {
            dbManager.updateChildScore(result[0], score, currentChild.getChildId());
            dbManager.disconnect();
        }
    }
    
    public static void saveSecondStageScore()
    {
        DatabaseManager dbManager = new DatabaseManager();
        
        int userId = StageManager.getCurrentUser().getUserId();
        int childId = currentChild.getChildId();
        
        
        if(dbManager.connect())
        {
            for (int i = 1; i <= 20; i++) 
            {
                Question q = questionMap.get(i);
                String qAnswer;
                
                if(q.getQuestionAnswer() == QuestionAnswer.YES)
                    qAnswer = "Yes";
                else
                    qAnswer = "No";
                
                String qNotes = q.getQuestionNotes();
                String fResult;
                String fAnswers;
                
                if(flaggedQuestions.contains(i))
                {
                    FollowUpFlow followUp = followUpMap.get(i);
                    followUp.resetCurrentNode();
                    
                    fResult = followUp.getFinalResult();
                    fAnswers = followUp.getFollowUpNodeResults();
                }
                else
                {
                    fResult = "N/A";
                    fAnswers = "N/A";
                }
                
                dbManager.writeDiagnosisToDatabase(userId, childId, i, qAnswer, qNotes, fResult, fAnswers);
            }
            dbManager.disconnect();
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
        }
        else
        {
            if(failedQuestions >= 2) riskId = 4;
            else riskId = 5;
        }
        
        if(db.connect())
        {
            riskText = db.getResultInfo(riskId, stage);
            db.disconnect();
        }
        return riskText;
    }
    
    public static void resetQuestionaireManager()
    {
        DatabaseManager dbManager = new DatabaseManager();
        flaggedQuestions = new ArrayList<>();
        failedQuestions = 0;
        
        if(dbManager.connect())
        {
            //Reset the question/followUp maps
            dbManager.loadQuestionList();
            dbManager.loadFollowUpList();
            
            //If the diagnosis was in progress also remove the current child from the db
            if(StageManager.getInProgress())
                dbManager.removeCurrentChild(currentChild.getChildId());
            
            dbManager.disconnect();
        }
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
        return followUpMap.get(fNumber);
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
