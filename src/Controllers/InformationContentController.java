package Controllers;

import Managers.DatabaseManager;
import Classes.FormTextLoader;
import Managers.LanguageManager;
import Managers.StageManager;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

public class InformationContentController implements Initializable 
{
    @FXML AnchorPane rootPane;
    @FXML VBox vboxMainInfoContent;
    
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
    }    
    
    @FXML public void btnMCHAT_Action(ActionEvent event)
    {
        StageManager.loadContentScene(StageManager.MCHATINFO);
        LanguageManager.setFormText("MCHATInfo", StageManager.getRootScene());
        
        //Closes the menu drawer on the main form when the information btn is clicked
        StageManager.getMainFormController().closeMenuDrawer();
    }
    
    @FXML public void btnNAO_Action(ActionEvent event)
    {
        StageManager.loadContentScene(StageManager.NAOINFO);
        LanguageManager.setFormText("MCHATInfo", StageManager.getRootScene());
        
        //Closes the menu drawer on the main form when the information btn is clicked
        StageManager.getMainFormController().closeMenuDrawer();
    }
}
