package Controllers;

import Managers.DatabaseManager;
import Classes.FormTextLoader;
import Classes.QuestionSet;
import Managers.QuestionaireManager;
import Managers.SettingsManager;
import Managers.StageManager;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class MCHATInfoContentController implements Initializable 
{
    @FXML Label lblSetName;
    @FXML VBox vboxInfoContent;

    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {
        QuestionSet questionSet = QuestionaireManager.getCurrentQuestionSet();
        lblSetName.setText(questionSet.getSetName());
        
        FormTextLoader textLoader = new FormTextLoader(vboxInfoContent);
        ArrayList<String> mchatInfo = questionSet.getInformation();
        
        textLoader.setAllVboxInformation(mchatInfo);
    }
    
    public void btnBack_Action(ActionEvent event)
    {
        StageManager.loadContentScene(StageManager.MAININFO);
    }
}
