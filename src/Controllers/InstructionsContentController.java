package Controllers;

import Managers.DatabaseManager;
import Managers.StageManager;
import Classes.FormTextLoader;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;

public class InstructionsContentController implements Initializable 
{
    @FXML private VBox vboxInstructionsContent;

    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {
        DatabaseManager dbManager = new DatabaseManager();
        FormTextLoader textLoader = new FormTextLoader(vboxInstructionsContent);
        ArrayList<String> instrInfo = new ArrayList<>();
        
        if(dbManager.connect())
        {
            instrInfo = dbManager.loadInformationData(1, "INSTRUCTIONS");
            dbManager.disconnect();
        }
        textLoader.setTextVboxInformation(instrInfo);
    }    
    
    @FXML private void btnContinue_Action(ActionEvent event)
    {
        StageManager.loadContentScene(StageManager.DETAILS);
    }
    
    @FXML private void btnInformation_Action(ActionEvent event)
    {
        StageManager.loadContentScene(StageManager.MAININFO);
        
        //Sets the underlined side menu button to be the info page
        StageManager.getMainFormController().setSelectedMenuButton("Info");
    }
}
