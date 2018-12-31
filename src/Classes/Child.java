package Classes;

public class Child 
{
    private final int childID;
    private final String childName;
    private final int childAge;
    private final String childGender;
    
    private String resultText;
    private int resultScore;
    
    public Child(int id, String name, int age, String gender)
    {
        childID= id;
        childName = name;
        childAge = age;
        childGender = gender;
    }
    
    public int getChildId() { return childID; }
    public String getChildName() { return childName; }
    public int getChildAge() { return childAge; }
    public String getChildGender() { return childGender; }
    public String getResultText() { return resultText; }
    public int getResultScore() { return resultScore; }
    
    public void setResultText(String text) { resultText = text; }
    public void setResultScore(int score) { resultScore = score; }
}
