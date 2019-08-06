package FYP_Application;

import Classes.User;
import Managers.StageManager;
import Managers.DatabaseManager;
import Managers.LanguageManager;
import Managers.QuestionaireManager;
import Managers.SettingsManager;
import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import javafx.application.Application;
import javafx.stage.Stage;

public class FYP_Application extends Application 
{
    private boolean rememberedUser;
    
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage stage) throws Exception 
    {
        setLocalisedDBPath();
        
        rememberedUser = false;
        LoadFromDatabase();
        
        if(rememberedUser)
            StageManager.loadForm(StageManager.MAIN, stage);
        else
            StageManager.loadForm(StageManager.LOGIN, stage);
        
        
    }
    
    private void setLocalisedDBPath()
    {
        try
        {
            //Get directory of current FYP class
            Path path = Paths.get(new File(FYP_Application.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath());
            
            //Go up two directories
            path = path.getParent().getParent();
            
            //Append the Database location to the path
            String dbPath = path.resolve("Database/DiagnosisData.db").toString();
            
            SettingsManager.setDBConnString(dbPath);
        } 
        catch (URISyntaxException ex) 
        {
            System.out.println("Failed when getting localised DatabasePath");
        }
    }
    
    private void LoadFromDatabase()
    {
        DatabaseManager db = new DatabaseManager();
        String questionSet = "M-CHAT-R/F";
        String setLanguage = "English";
        
        //Connect to the SQLite database and load the neccessary data into
        //static variables on the DatabaseManager class
        if(db.connect())
        {
            //Check to see if there is a user that has been 
            int userId = db.checkForRememberedUser();
            User user;
            
            if(userId != -1)
            {
                user = db.loadUser(userId);
                
                if(user != null)
                {
                    StageManager.setCurrentUser(user);
                    db.loadApplicationSettings(userId);
                    
                    questionSet = SettingsManager.getQuestionSet();
                    setLanguage = SettingsManager.getSetLanguage();
                    rememberedUser = true;
                }
            }
            else
                setDefaultSettings();
            
            LanguageManager.loadFormText();
            db.loadQuestionSetNames();
            db.loadQuestionSetsMap();
            db.loadFollowUpList();
            db.loadLanguageList();
            db.disconnect();
            
            QuestionaireManager.setCurrentQuestionSet(questionSet, setLanguage);
        }
    }
    
    /**
     * If there is no user remembered set the settings to 
     * the default values.
     */
    private void setDefaultSettings()
    {
        LanguageManager.setLanguage("English");
        SettingsManager.setQuestionSet("M-CHAT-R/F");
        SettingsManager.setSetLanguage("English");
    }
}
