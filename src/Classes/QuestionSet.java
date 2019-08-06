package Classes;

import java.util.ArrayList;
import java.util.HashMap;

public class QuestionSet 
{
    private final String setName;
    private final int numberOfQuestions;
    private final HashMap<Integer, Question> questionSet;
    private final ArrayList<String> activeLanguages;
    private final ArrayList<String> information;
    private final ScoringAlgorithm scoringAlgorithm;
    
    private String currentLanguage;
    
    public QuestionSet(String name, int numOfQs, HashMap<Integer, Question> qSet, ArrayList<String> languages, 
            ArrayList<String> info, ScoringAlgorithm sa) 
    {
        setName = name;
        numberOfQuestions = numOfQs;
        questionSet = qSet; 
        activeLanguages = languages;
        information = info;
        scoringAlgorithm = sa;
    }
    
    public void resetAnswers()
    {
        for (int i = 1; i <= numberOfQuestions; i++) 
        {
            questionSet.get(i).setQuestionAnswer(null);
            questionSet.get(i).setQuestionNotes(null);
        }
    }
    
    public String getSetName() { return setName; }
    public int getNumberOfQuestions() { return numberOfQuestions; }
    public HashMap<Integer, Question> getQuestionSet() { return questionSet; }
    public Question getQuestion(int num) { return questionSet.get(num); }
    public ArrayList<String> getActiveLanguages() { return activeLanguages; }
    public ArrayList<String> getInformation() { return information; }
    public ScoringAlgorithm getScoringAlgorithm() { return scoringAlgorithm; }
    public String getCurrentLanguage() { return currentLanguage; }
    
    public void setCurrentLanguage(String language) { currentLanguage = language; }
}
