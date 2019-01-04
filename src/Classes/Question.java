package Classes;

public class Question 
{
    private final int questionID;
    private final String questionText;
    private QuestionAnswer questionAnswer;
    private String questionNotes;
    
    public Question(int id, String qText)
    {
        questionID = id;
        questionText = qText;
    }
    
    public int getQuestionId() { return questionID; }
    public String getQuestionText() { return questionText; }
    public QuestionAnswer getQuestionAnswer() { return questionAnswer; }
    public String getQuestionNotes() { return questionNotes; }
    
    public void setQuestionAnswer(QuestionAnswer answer) { questionAnswer = answer; }
    public void setQuestionNotes(String notes) { questionNotes = notes; }
}