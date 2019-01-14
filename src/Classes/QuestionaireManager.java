package Classes;

import java.util.ArrayList;
import java.util.HashMap;

public class QuestionaireManager 
{
    private static HashMap<Integer, Question> questionMap;
    private static Question currentQuestion;
    private static ArrayList<Integer> flaggedQuestions;
    private static int currentChildId;
    
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
    
    public static void saveFinalScore()
    {
        DatabaseManager dbManager = new DatabaseManager();
        String text = getResultInfo();
        String[] result = text.split("\n");
        int score = flaggedQuestions.size();
        
        if(dbManager.connect())
        {
            dbManager.updateChildScore(result[0], score, currentChildId);
            dbManager.disconnect();
            
            flaggedQuestions = new ArrayList<>();
        }
    }
    
    public static String getResultInfo()
    {
        DatabaseManager db = new DatabaseManager();
        String riskText = "";
        
        int score = flaggedQuestions.size();
        int riskId;
        
        if(score <= 2) riskId = 1;
        else if(score <= 7) riskId = 2;
        else riskId = 3;
        
        if(db.connect())
        {
            riskText = db.getResultInfo(riskId);
            db.disconnect();
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
    
    public static void setCurrentChildId(int id)
    {
        currentChildId = id;
    }
}
