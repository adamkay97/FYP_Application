package Controllers;

import Classes.DatabaseManager;
import Classes.StageManager;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class InformationContentController implements Initializable 
{
    @FXML VBox vboxInfoContent;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {
        
    }    
    
    @FXML public void btnMCHAT_Action(ActionEvent event)
    {
        StageManager.loadContentScene(StageManager.MCHATINFO);
    }
}
