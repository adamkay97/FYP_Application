package ControlControllers;

import Controllers.FollowUpContentController;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

public class ResultControlController implements Initializable 
{
    @FXML private Label lblResult;
    
    private FollowUpContentController followUpController;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
    @FXML
    public void btnNext_Action(ActionEvent event) 
    {
        followUpController.loadNextFollowUpPart(null);
    }
    
    public void setResultText(String result) { lblResult.setText(result); }
    public void setFollowUpController(FollowUpContentController controller) { followUpController = controller; }
}
