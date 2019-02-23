package Classes;

public class SettingsManager 
{
    private static String robotConnectionURL;
    private static int robotVolume;
    private static String noteMethod;
    
    public static void initialiseSettings(String url, int volume, String method)
    {
        robotConnectionURL = url;
        robotVolume = volume;
        noteMethod = method;
    }
    
    public static void saveCurrentSettings()
    {
        DatabaseManager dbManager = new DatabaseManager();
        
        if(dbManager.connect())
        {
            int userId = StageManager.getCurrentUser().getUserId();
            dbManager.updateUserSettings(userId, robotConnectionURL, robotVolume, noteMethod);
            dbManager.disconnect();
        }
    }
    
    public static String getRobotConnection() { return robotConnectionURL; }
    public static int getRobotVolume() { return robotVolume; }
    public static String getNoteMethod() { return noteMethod; }
    
    public static void setRobotConnection(String url) { robotConnectionURL = url; }
    public static void setRobotVolume(int volume) { robotVolume = volume; }
    public static void setNoteMethod(String method) { noteMethod = method; }
}
