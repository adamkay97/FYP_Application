package Classes;

public class Question 
{
    private final int questionID;
    private final String questionText;
    private QuestionAnswer questionAnswer;
    
    public Question(int id, String qText)
    {
        questionID = id;
        questionText = qText;
    }
    
    public int getQuestionId()
    {
        return questionID;
    }
    
    public String getQuestionText()
    {
        return questionText;
    }
    
    public void setQuestionAnswer(QuestionAnswer answer)
    {
        questionAnswer = answer;
    }
}
