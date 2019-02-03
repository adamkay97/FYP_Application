package Controllers;

import com.jfoenix.controls.JFXRadioButton;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;

public class YesNoExampleControlController implements Initializable 
{
    @FXML private JFXRadioButton radBtnYes;
    @FXML private JFXRadioButton radBtnNo;
    @FXML private Label lblExampleText;
    private ToggleGroup yesNoGroup;
    
    private ChecklistControlController checklistController;
    private String passFail;
    private boolean selected;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {
        selected = false;       
    }    
    
    public void setExampleControl(String text, String pF, ToggleGroup group)
    {
        yesNoGroup = group;
        
        lblExampleText.setText(text);
        radBtnYes.setToggleGroup(yesNoGroup);
        radBtnNo.setToggleGroup(yesNoGroup);
        
        radBtnYes.setUserData(text + " = Yes");
        radBtnNo.setUserData(text + " = No");
        passFail = pF;
        
        checklistController();
    }
    
    private void checklistController()
    {
         //Add listener to toggle group that sets 
        yesNoGroup.selectedToggleProperty().addListener(
            (ObservableValue<? extends Toggle> ov, Toggle old_toggle, Toggle new_toggle) -> 
            {
                if (yesNoGroup.getSelectedToggle() != null) 
                {
                    if(yesNoGroup.getSelectedToggle().equals(radBtnYes))
                        checklistController.saveYesNoAnswer("YES", lblExampleText.getText(), passFail, selected);
                    else
                        checklistController.saveYesNoAnswer("NO", lblExampleText.getText(), passFail, selected);
                    
                    selected = true;
                } 
            });
    }
    
    public void setCheckListController(ChecklistControlController controller) { checklistController = controller; } 
}
