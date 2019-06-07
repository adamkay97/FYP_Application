package FYP_Application;

import Classes.User;
import Managers.StageManager;
import Managers.DatabaseManager;
import Managers.LanguageManager;
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
        rememberedUser = false;
        LoadFromDatabase();
        
        if(rememberedUser)
            StageManager.loadForm(StageManager.MAIN, new Stage());
        else
            StageManager.loadForm(StageManager.LOGIN, stage);
        
    }
    
    private void LoadFromDatabase()
    {
        DatabaseManager db = new DatabaseManager();
        
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
                    LanguageManager.loadFormText();
                    
                    rememberedUser = true;
                }
            }
            
            db.loadQuestionList();
            db.loadFollowUpList();
            db.disconnect();
        }
    }  
}
