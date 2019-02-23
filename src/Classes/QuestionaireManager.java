package Classes;

import Enums.QuestionAnswer;
import java.util.ArrayList;
import java.util.HashMap;

public class QuestionaireManager 
{
    private static HashMap<Integer, Question> questionMap;
    private static HashMap<Integer, FollowUpFlow> followUpMap;
    private static Child currentChild;
    private static Question currentQuestion;
    
    private static ArrayList<Integer> flaggedQuestions;
    
    private static int stageOneScore;
    private static String stageOneRisk;
    private static int failedQuestions;
    private static boolean followUpCompleted = false;
    
    /**
     * Save the current questions answer and add question to flagged list if it 
     * was answered with a risk of ASD.
     * @param qNumber number of the question
     * @param qAnswer question answer
     * @param qNotes question notes
     */
    public static void saveQuestionAnswer(int qNumber, QuestionAnswer qAnswer, String qNotes)
    {
        currentQuestion.setQuestionAnswer(qAnswer);
        currentQuestion.setQuestionNotes(qNotes);
        
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
     
    /**
     * After first Stage (M-CHAT-R) has been completed save the score to 
     * the child. If they don't need to carry out any follow up questions
     * write that child's question answers and diagnosis results to the database
     * @param followUp boolean that says whether or not the follow up questions
     * need to be administered.
     */
    public static void saveFirstStageScore(boolean followUp)
    {
        DatabaseManager dbManager = new DatabaseManager();
        String text = getResultInfo(1);
        String[] result = text.split("\n");
        
        //Save values on QuestionManager for writing the scores to DiagnosisResults
        //either after first or second stage.
        stageOneScore = flaggedQuestions.size();
        stageOneRisk = result[0];
        
        if(!followUp)
        {
            if(dbManager.connect())
            {
                int userId = StageManager.getCurrentUser().getUserId();
                int childId = currentChild.getChildId();
                
                //Loop through each question in the questionMap
                for (int i = 1; i <= 20; i++) 
                {
                    Question q = questionMap.get(i);
                    String qAnswer;

                    if(q.getQuestionAnswer() == QuestionAnswer.YES)
                        qAnswer = "Yes";
                    else
                        qAnswer = "No";

                    String qNotes = q.getQuestionNotes();
                    
                    //Write each questions answer and notes to db, but as no follow up is required here just n/a.
                    dbManager.writeDiagnosisToDatabase(userId, childId, i, qAnswer, qNotes, "N/A", "N/A");
                }
                
                dbManager.writeChildDiagnosisResult(userId, childId, stageOneScore, stageOneRisk, 0, "Negative");
                dbManager.disconnect();
            }
        }
    }
    
    /**
     * After the Second and final stage of the diagnosis has been completed
     * gather all the data from the questions and follow up questions and write them 
     * to the database for review purposes, before writing the overall result to the 
     * DiagnosisResults table.
     */
    public static void saveSecondStageScore()
    {
        DatabaseManager dbManager = new DatabaseManager();
        
        int userId = StageManager.getCurrentUser().getUserId();
        int childId = currentChild.getChildId();
        
        if(dbManager.connect())
        {
            //Loop through each question in the questionMap
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
                    //If the current question of the loop was one of the failed questions
                    //get the corresponding followUp question and concat the answers together
                    FollowUpFlow followUp = followUpMap.get(i);
                    followUp.resetCurrentNode();
                    
                    fResult = followUp.getFinalResult();
                    fAnswers = followUp.getFollowUpNodeResults();
                }
                else
                {
                    //Else if this question was not required in the followUp just write n/a
                    fResult = "N/A";
                    fAnswers = "N/A";
                }
                
                dbManager.writeDiagnosisToDatabase(userId, childId, i, qAnswer, qNotes, fResult, fAnswers);
            }
            
            //Work out whether the overall screening is positive or negative and then write the full result to the db
            String overallScreening = failedQuestions >= 2 ? "Positive" : "Negative";
            dbManager.writeChildDiagnosisResult(userId, currentChild.getChildId(), stageOneScore, 
                    stageOneRisk, failedQuestions, overallScreening);
            
            dbManager.disconnect();
        }
    }
    
    /**
     * Depending on which stage of the diagnosis, read the text result saved in
     * the database for the result information provided at the end of the stage
     * @param stage the current stage that the diagnosis is at (Either 1 or 2)
     * @return the text from the database as a string
     */
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
    
    /**
     * Resets all the static variables and question lists once the 
     * diagnosis has been completed so its ready for the next diagnosis
     */
    public static void resetQuestionaireManager()
    {
        DatabaseManager dbManager = new DatabaseManager();
        flaggedQuestions = new ArrayList<>();
        stageOneScore = 0;
        stageOneRisk = "";
        failedQuestions = 0;
        
        if(dbManager.connect())
        {
            //Reset the question/followUp maps
            dbManager.loadQuestionList();
            dbManager.loadFollowUpList();
            
            //If the diagnosis was in progress also remove the current child from the db
            //and decrease the AutoIncrement value in sqlite_sequence table
            if(StageManager.getInProgress())
            {
                dbManager.removeCurrentChild(currentChild.getChildId());
                dbManager.decreaseSequenceID("Children");
            }
            dbManager.disconnect();
        }
    }
     
    public static int getQuestionaireScore()
    {
        return flaggedQuestions.size();
    }
    
    public static Question getQuestion(int qNumber)
    {
        currentQuestion = questionMap.get(qNumber);
        return currentQuestion;
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
