package ControlControllers;

import Controllers.FollowUpContentController;
import Enums.FlowBranchEnum;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

public class YesNoControlController implements Initializable 
{
    @FXML private Label lblQuestionText;    
    
    private FollowUpContentController followUpController;

    @Override
    public void initialize(URL url, ResourceBundle rb) { }   
    
    @FXML public void btnNo_Action(ActionEvent event) 
    {
        FlowBranchEnum result = FlowBranchEnum.No;      
        
        followUpController.setCurrentNodeAnswer(lblQuestionText.getText() + "=No");
        followUpController.loadNextFollowUpPart(result);
    }

    @FXML public void btnYes_Action(ActionEvent event) 
    {
        FlowBranchEnum result = FlowBranchEnum.Yes;
        
        followUpController.setCurrentNodeAnswer(lblQuestionText.getText() + "=Yes");
        followUpController.loadNextFollowUpPart(result);
    }
    
    public void setQuestionText(String text) { lblQuestionText.setText(text); }
    public void setFollowUpController(FollowUpContentController controller) { followUpController = controller; }
}
