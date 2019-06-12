package Controllers;

import Enums.ButtonTypeEnum;
import Managers.LanguageManager;
import Managers.StageManager;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.*;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class PopUpMessageController implements Initializable 
{    
    @FXML private AnchorPane mainAnchorPane;
    @FXML private Pane pnlOkBtn;
    @FXML private Pane pnlYesNoBtn;
    @FXML private Label lblMessage;
    @FXML private Label lblPopupHeader;
    @FXML public Button btnOk;
    @FXML public Button btnYes;
    @FXML public Button btnNo;

    @Override
    public void initialize(URL url, ResourceBundle rb) {}    
    
    public void setPopupContent(String headerText, String messageText, ButtonTypeEnum buttonType)
    {
        lblPopupHeader.setText(headerText);
        lblMessage.setText(messageText);
        
        switch(buttonType)
        {
            case OK:
                pnlOkBtn.setVisible(true);
                break;
            case YESNO:
                pnlYesNoBtn.setVisible(true);
                break;
        }
    }
    
    @FXML public void btnOk_Action(ActionEvent e)
    {
        Stage currentStage = (Stage)mainAnchorPane.getScene().getWindow();
        currentStage.close();
    }
}
