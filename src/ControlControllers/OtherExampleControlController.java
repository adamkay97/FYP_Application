package ControlControllers;

import Managers.LanguageManager;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

public class OtherExampleControlController implements Initializable 
{
    @FXML private Label lblExampleText;
    @FXML private TextArea txtAreaOther;
    
    private ChecklistControlController checklistController;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {}    
    
    public void setExampleControl(String text) 
    { 
        HashMap<String,String> formText = LanguageManager.getSpecifiedText("StageTwoOtherExample");
        
        lblExampleText.setText(text); 
        txtAreaOther.setPromptText(formText.get("O2"));
        
        //Add listener to text area, set the text to variable on checkListController
        txtAreaOther.textProperty().addListener((observ, oVal, nVal) -> 
        {
            checklistController.setOtherText(nVal);
        });
    }
    
    public void setCheckListController(ChecklistControlController controller) { checklistController = controller; } 
    
}
