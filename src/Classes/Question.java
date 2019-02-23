package Classes;

import Enums.QuestionAnswer;

public class Question 
{
    private final int questionID;
    private final String questionText;
    private final String questionInstructions;
    private QuestionAnswer questionAnswer;
    private String questionNotes;
    
    public Question(int id, String qText, String instruction)
    {
        questionID = id;
        questionText = qText;
        questionInstructions = instruction;
    }
    
    public int getQuestionId() { return questionID; }
    public String getQuestionText() { return questionText; }
    public String getQuestionInstructions() { return questionInstructions; }
    public QuestionAnswer getQuestionAnswer() { return questionAnswer; }
    public String getQuestionNotes() { return questionNotes; }
    
    public void setQuestionAnswer(QuestionAnswer answer) { questionAnswer = answer; }
    public void setQuestionNotes(String notes) { questionNotes = notes; }
}