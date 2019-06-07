package Controllers;

import Managers.LanguageManager;
import Managers.StageManager;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class PopUpInstructionsController implements Initializable 
{
    @FXML private AnchorPane mainAnchorPane;
    @FXML private ImageView imgViewInstructions;
    @FXML private Label lblMessage;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {
        LanguageManager.setFormText("PopupInstructions", StageManager.getRootScene());
    }    
    
    public void setInstructionsPopupContent(String messageText, int qIndex)
    {
        lblMessage.setText(messageText);
        Image img = new Image(String.format("Images/NaoImgQ%d.jpg", qIndex));
        imgViewInstructions.setImage(img);
    }
    
    @FXML public void btnOk_Action(ActionEvent e)
    {
        Stage currentStage = (Stage)mainAnchorPane.getScene().getWindow();
        currentStage.close();
    }
    
}
