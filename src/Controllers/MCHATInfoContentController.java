package Controllers;

import Managers.DatabaseManager;
import Classes.FormTextLoader;
import Managers.SettingsManager;
import Managers.StageManager;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;

public class MCHATInfoContentController implements Initializable 
{
    @FXML VBox vboxInfoContent;

    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {
        DatabaseManager dbManager = new DatabaseManager();
        FormTextLoader textLoader = new FormTextLoader(vboxInfoContent);
        ArrayList<String> mchatInfo = new ArrayList<>();
        
        if(dbManager.connect())
        {
            mchatInfo = dbManager.loadInformationData(SettingsManager.getQuestionSet());
            dbManager.disconnect();
        }
        textLoader.setAllVboxInformation(mchatInfo);
    }
    
    public void btnBack_Action(ActionEvent event)
    {
        StageManager.loadContentScene(StageManager.MAININFO);
    }
}
