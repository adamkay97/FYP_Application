package Controllers;

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
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }   
    
    @FXML
    void btnNo_Action(ActionEvent event) {

    }

    @FXML
    void btnYes_Action(ActionEvent event) {

    }
    
    public void setFollowUpController(FollowUpContentController controller) { followUpController = controller; } 
    public void setQuestionText(String text) { lblQuestionText.setText(text); }
}
