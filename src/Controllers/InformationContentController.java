package Controllers;

import Managers.DatabaseManager;
import Classes.FormTextLoader;
import Managers.SettingsManager;
import Managers.StageManager;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

public class InformationContentController implements Initializable 
{
    @FXML AnchorPane rootPane;
    @FXML VBox vboxMainInfoContent;
    @FXML Button btnMCHAT;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {
        DatabaseManager dbManager = new DatabaseManager();
        FormTextLoader textLoader = new FormTextLoader(vboxMainInfoContent);
        ArrayList<String> mainInfo = new ArrayList<>();
        
        if(dbManager.connect())
        {
            mainInfo = dbManager.loadInformationData("MAIN");
            dbManager.disconnect();
        }
        textLoader.setTextVboxInformation(mainInfo);
        
        //Set button text to relevant question set
        Platform.runLater(() -> {
            btnMCHAT.setText(SettingsManager.getQuestionSet());
        });
    }    
    
    @FXML public void btnMCHAT_Action(ActionEvent event)
    {
        StageManager.loadContentScene(StageManager.MCHATINFO);
        
        //Closes the menu drawer on the main form when the information btn is clicked
        StageManager.getMainFormController().closeMenuDrawer();
    }
    
    @FXML public void btnNAO_Action(ActionEvent event)
    {
        StageManager.loadContentScene(StageManager.NAOINFO);
        
        //Closes the menu drawer on the main form when the information btn is clicked
        StageManager.getMainFormController().closeMenuDrawer();
    }
}
