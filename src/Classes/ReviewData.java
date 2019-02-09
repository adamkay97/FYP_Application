package Classes;

public class ReviewData 
{
    private final int questionNumber;
    private final String questionText;
    private final String questionAnswer;
    private final String questionNotes;
    private final String followUpResult;
    private final String followUpText;
    
    public ReviewData(int num, String text, String answer, String notes, String result, String fText)
    {
        questionNumber = num;
        questionText = text;
        questionAnswer = answer;
        questionNotes = notes;
        followUpResult = result;
        followUpText = fText;
    }
    
    public int getQuestionNumber() { return questionNumber; }
    public String getQuestionText() { return questionText; }
    public String getQuestionAnswer() { return questionAnswer; }
    public String getQuestionNotes() { return questionNotes; }
    public String getFollowUpResult() { return followUpResult; }
    public String getFollowUpText() { return followUpText; }
    
}
