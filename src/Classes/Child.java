package Classes;

public class Child 
{
    private int childID;
    private final int currentUserID;
    private final String childName;
    private final int childAge;
    private final String childGender;
    
    private String resultText;
    private int resultScore;
    
    public Child(int userId, String name, int age, String gender)
    {
        currentUserID = userId;
        childName = name;
        childAge = age;
        childGender = gender;
    }
    
    public Child(int id, int userId, String name, int age, String gender, String text, int score)
    {
        childID= id;
        currentUserID = userId;
        childName = name;
        childAge = age;
        childGender = gender;
        resultText = text;
        resultScore = score;
    }
    
    public int getChildId() { return childID; }
    public int getCurrentUserId() { return currentUserID; }
    public String getChildName() { return childName; }
    public int getChildAge() { return childAge; }
    public String getChildGender() { return childGender; }
    public String getResultText() { return resultText; }
    public int getResultScore() { return resultScore; }
    
    public void setChildId(int id) { childID = id; } 
    public void setResultText(String text) { resultText = text; }
    public void setResultScore(int score) { resultScore = score; }
}
