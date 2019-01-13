package Controllers;

import Classes.DatabaseManager;
import Classes.FormTextLoader;
import Classes.StageManager;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;

public class InformationContentController implements Initializable 
{
    @FXML VBox vboxMainInfoContent;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {
        DatabaseManager dbManager = new DatabaseManager();
        FormTextLoader textLoader = new FormTextLoader(vboxMainInfoContent);
        ArrayList<String> mainInfo = new ArrayList<>();
        
        if(dbManager.connect())
        {
            mainInfo = dbManager.loadInformationData(1, "MAIN");
            dbManager.disconnect();
        }
        textLoader.setTextVboxInformation(mainInfo);
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