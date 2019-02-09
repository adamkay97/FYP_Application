package Controllers;

import Classes.RobotManager;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

public class SettingsContentController implements Initializable 
{
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
    @FXML public void btnTest_Action(ActionEvent event) 
    {
        //boolean connect = RobotManager.connectToRobot();
    }
    
}
