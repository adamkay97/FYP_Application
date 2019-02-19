package Classes;

public class Child 
{
    private int childID;
    private final int currentUserID;
    private final String childName;
    private final int childAge;
    private final String childGender;
    
    private DiagnosisResult diagnosisResult;
    
    public Child(int userId, String name, int age, String gender)
    {
        currentUserID = userId;
        childName = name;
        childAge = age;
        childGender = gender;
    }
    
    public Child(int id, int userId, String name, int age, String gender, DiagnosisResult dResult)
    {
        childID= id;
        currentUserID = userId;
        childName = name;
        childAge = age;
        childGender = gender;
        diagnosisResult = dResult;
    }
    
    public int getChildId() { return childID; }
    public int getCurrentUserId() { return currentUserID; }
    public String getChildName() { return childName; }
    public int getChildAge() { return childAge; }
    public String getChildGender() { return childGender; }
    public DiagnosisResult getDiagnosisResult() { return diagnosisResult; }
    
    public void setChildId(int id) { childID = id; } 
}
