package Managers;

import java.io.File;

public class SettingsManager 
{
    private static final LogManager logManager = new LogManager();
    
    private static boolean usesNaoInteraction;
    private static String robotConnectionURL;
    private static int robotVolume;
    private static String questionSet;
    private static String questionSetLanguage;
    private static String language;
    private static String noteMethod;
    private static String audioFileLocation;
    
    private static String DBConnString;
    
    /**
     * Initialise the SettingsManagers static variables when loading them from the database
     * @param usesNao Whether the interaction with the robot is actually wanted
     * @param url URL of the robot for connection
     * @param volume Volume of the robot
     * @param qSet The current set of questions to be used for diagnosis
     * @param qSetLang The language for the set of questions to be used for diagnosis
     * @param lang Language of the application
     * @param method The way notes are taken 
     * @param audioPath The path for which the audio files will be saved 
     */
    public static void initialiseSettings(String usesNao, String url, int volume, 
        String qSet, String qSetLang, String lang, String method, String audioPath)
    {
        usesNaoInteraction = usesNao.equals("YES");
        robotConnectionURL = url;
        robotVolume = volume;
        questionSet = qSet;
        questionSetLanguage = qSetLang;
        language = lang;
        noteMethod = method;
        audioFileLocation = audioPath;
        
        LanguageManager.setLanguage(language);
    }
    
    /**
     * Using the database manager, call the update user settings method with the
     * current saved settings
     */
    public static void saveCurrentSettings()
    {
        DatabaseManager dbManager = new DatabaseManager();
        
        if(dbManager.connect())
        {
            int userId = StageManager.getCurrentUser().getUserId();
            String usesNao = usesNaoInteraction ? "YES" : "NO";
            dbManager.updateUserSettings(userId, usesNao, robotConnectionURL, robotVolume, 
                                            questionSet, questionSetLanguage, language, noteMethod, audioFileLocation);
            LanguageManager.setLanguage(language);
            
            dbManager.disconnect();
        }
    }
    
    /**
     * If the user leaves the diagnosis half way through and are using the audio note taking method
     * delete that folder with the sound files in.
     */
    public static void deleteCurrentAudioFiles()
    {
        if(noteMethod.equals("Audio"))
        {
            int childId = QuestionaireManager.getCurrentChild().getChildId();
            String childName = QuestionaireManager.getCurrentChild().getChildName();
            String childFolder = String.format("%d-%s", childId, childName); 

            String audioFilePath = String.format("%s\\%s\\", SettingsManager.getAudioFileLocation(), childFolder);

            File audioFolder = new File(audioFilePath);
            if(audioFolder.delete())
                logManager.InfoLog("Successfully deleted audio folder when leaving diagnosis");
            else
                logManager.ErrorLog("Failed to delete audio folder when leaving diagnosis");
        }
    }
    
    public static boolean getUsesNaoRobot() { return usesNaoInteraction; }
    public static String getRobotConnection() { return robotConnectionURL; }
    public static int getRobotVolume() { return robotVolume; }
    public static String getQuestionSet() { return questionSet; }
    public static String getSetLanguage() { return questionSetLanguage; }
    public static String getLanguage() { return language; }
    public static String getNoteMethod() { return noteMethod; }
    public static String getAudioFileLocation() { return audioFileLocation; }
    
    public static void setUsesNaoRobot(boolean usesNao) { usesNaoInteraction = usesNao; }
    public static void setRobotConnection(String url) { robotConnectionURL = url; }
    public static void setRobotVolume(int volume) { robotVolume = volume; }
    public static void setQuestionSet(String qSet) { questionSet = qSet; }
    public static void setSetLanguage(String lang) { questionSetLanguage = lang; }
    public static void setLanguage(String lang) { language = lang; }
    public static void setNoteMethod(String method) { noteMethod = method; }
    public static void setAudioFileLocation(String path) { audioFileLocation = path; }
    
    public static String getDBConnString() { return DBConnString; }
    public static void setDBConnString(String conn) { DBConnString = conn; }
}
