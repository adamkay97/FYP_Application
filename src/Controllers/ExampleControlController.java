package Controllers;

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
        //TODO:: SaveAnswer
        followUpController.loadNextFollowUpPart(null);
    }
    
    public void setFollowUpController(FollowUpContentController controller) { followUpController = controller; } 
    public void setQuestionText(String text) { lblQuestionText.setText(text); }
}
