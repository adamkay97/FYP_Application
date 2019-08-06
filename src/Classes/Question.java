package Classes;

import Enums.QuestionAnswer;
import java.util.HashMap;

public class Question 
{
    private final int questionID;
    private final String behaviourName;
    private final HashMap<String, String> questionText;
    private final HashMap<String, String> questionInstructions;
    private final QuestionAnswer atRiskResponse;
    private QuestionAnswer questionAnswer;
    private String questionNotes;
    
    public Question(int id, String behaviour, HashMap<String, String> qText, 
            HashMap<String, String> instruction, String risk)
    {
        questionID = id;
        questionText = qText;
        questionInstructions = instruction;
        behaviourName = behaviour;
        
        if(risk.equals("Yes"))   
            atRiskResponse = QuestionAnswer.YES;
        else
            atRiskResponse = QuestionAnswer.NO;
    }
    
    public int getQuestionId() { return questionID; }
    public String getBehaviourName() { return behaviourName; }
    public String getQuestionText(String language) { return questionText.get(language); }
    public String getQuestionInstructions(String language) { return questionInstructions.get(language); }
    public QuestionAnswer getAtRiskResponse() { return atRiskResponse; }
    public QuestionAnswer getQuestionAnswer() { return questionAnswer; }
    public String getQuestionNotes() { return questionNotes; }
    
    public void setQuestionAnswer(QuestionAnswer answer) { questionAnswer = answer; }
    public void setQuestionNotes(String notes) { questionNotes = notes; }
}