package ControlControllers;

import Classes.PopupText;
import Managers.StageManager;
import Controllers.FollowUpContentController;
import Enums.ButtonTypeEnum;
import Managers.LanguageManager;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

public class ExampleControlController implements Initializable 
{
    @FXML private Button btnContinue;
    @FXML private TextArea txtExample;
    @FXML private Label lblQuestionText;
    
    private FollowUpContentController followUpController;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {}    
     

    @FXML public void btnContinue_Action(ActionEvent event) 
    {
        if(!txtExample.getText().equals(""))
        {
            followUpController.setCurrentNodeAnswer(lblQuestionText.getText() + "=" + txtExample.getText());
            followUpController.loadNextFollowUpPart(null);
        }
        else
        {
            PopupText popup = LanguageManager.getPopupText(20);
            StageManager.loadPopupMessage(popup.getHeader(), popup.getMessage(), ButtonTypeEnum.OK);
        }
    }
    
    public void setFollowUpController(FollowUpContentController controller) { followUpController = controller; } 
    public void setQuestionText(String text) { lblQuestionText.setText(text); }
}
