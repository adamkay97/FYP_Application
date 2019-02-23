/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers;

import Enums.ButtonTypeEnum;
import Classes.*;
import com.jfoenix.controls.*;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.*;

public class ChildDetailsContentController implements Initializable {

    @FXML private JFXRadioButton radBtnFemale;
    @FXML private JFXRadioButton radBtnMale;
    @FXML private JFXTextField txtChildsAge;
    @FXML private JFXTextField txtChildsName;
    
    private DatabaseManager dbManager;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {
        dbManager = new DatabaseManager();
    }    

    @FXML public void btnStartQuestions_Action(ActionEvent event)
    {
        if(validateUserInput())
        {
            //Creates child with data needed for database including current users id for review purposes
            int currentUserId = StageManager.getCurrentUser().getUserId();
            String name = txtChildsName.getText();
            int age = Integer.parseInt(txtChildsAge.getText());
            String gender = radBtnMale.isSelected() ? "Male" : "Female";
            
            Child child = new Child(currentUserId, name, age, gender);
            
            //Check if there is already a child created with the exact same info
            if(!checkChildExists(child))
            {
                if(saveChildInfo(child))
                {
                    //Check that there is a valid connection to the Robot
                    if(true)//RobotManager.getRobotConnected())
                    {
                        //Run start behaviour in seperate thread so the rest of the application is'nt held up
                        Thread robotThread = new Thread(() -> {
                            RobotManager.runStartEnd(true);
                        });
                        //robotThread.start();
                        
                        StageManager.loadContentScene(StageManager.QUESTIONAIRE);
                        StageManager.setInProgress(true);
                    }
                    else
                    {
                        //If cannot connect, point user to settings page where they can test the connection.
                        String msg = "There doesn't appear to be a valid connection to NAO. Please use the settings tab "
                                + "to access the connection details for NAO.";
                        StageManager.loadPopupMessage("Error", msg, ButtonTypeEnum.OK);
                    }
                }
            }
        }
        else
        {
            String messageText = "One or more fields have been left empty/incorrect.\n"
                    + "All the fields must be filled in correctly before you can proceed.";
            
            StageManager.loadPopupMessage("Error", messageText, ButtonTypeEnum.OK);
        }

    }
    
    private boolean saveChildInfo(Child child)
    {
        if(dbManager.connect())
        {
            if(!dbManager.writeChildToDatabase(child))
            {
                StageManager.loadPopupMessage("Error", "There was an issue with saving your information, "
                    + "please try again. If this error persists please contact support.", ButtonTypeEnum.OK);
                return false;
            }
            dbManager.disconnect();
            
            //Set the current childs id on the QuestionaireManager for saving the score
            //to the db at the end of the diagnosis.
            QuestionaireManager.setCurrentChild(child);
        }
        return true;
    }
    
    private boolean validateUserInput()
    {
        if(txtChildsName.getText().equals(""))
            return false;
        
        if(txtChildsAge.getText().equals("") || !isNumeric(txtChildsAge.getText()))
            return false;
        
        if(!radBtnMale.isSelected() && !radBtnFemale.isSelected())
            return false;
        
        return true;
    }
    
    private boolean checkChildExists(Child child)
    {
        boolean exists = false;
        
        if(dbManager.connect())
        {
            if(dbManager.checkChildExists(child))
            {
                StageManager.loadPopupMessage("Warning", "A child with these exact credentials has already been tested, "
                            + "you can see the results on the review page.", ButtonTypeEnum.OK);
                exists = true;
            }
            dbManager.disconnect();
        }
        
        return exists; 
    }
    
    private boolean isNumeric(String age) 
    {
        //Parse user input to see if the age input is numeric or not
        try {
            int i = Integer.parseInt(age);
        } 
        catch (NumberFormatException | NullPointerException ex) {
            return false;
        }
        return true;
    }
}
