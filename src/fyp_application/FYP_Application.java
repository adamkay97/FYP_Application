package fyp_application;

import Classes.*;
import javafx.application.Application;
import javafx.stage.Stage;

public class FYP_Application extends Application 
{
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage stage) throws Exception 
    {
        LoadFromDatabase();
        
        StageManager.loadForm(StageManager.MAIN, stage);
        //StageManager.loadForm(StageManager.LOGIN, stage);
    }
    
    private void LoadFromDatabase()
    {
        DatabaseManager db = new DatabaseManager();
        
        //Connect to the SQLite database and load the neccessary data into
        //static variables on the DatabaseManager class
        if(db.connect())
        {
            db.loadQuestionList();
            db.disconnect();
        }
    }  
}
