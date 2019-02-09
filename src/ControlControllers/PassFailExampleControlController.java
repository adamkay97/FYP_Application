package ControlControllers;

import com.jfoenix.controls.JFXRadioButton;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleGroup;

public class PassFailExampleControlController implements Initializable 
{
    @FXML private Label lblExampleText;
    @FXML private JFXRadioButton radBtn;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
    public void setExampleControl(String text, String pF, ToggleGroup toggleGroup)
    {
        lblExampleText.setText(text);
        radBtn.setToggleGroup(toggleGroup);
        radBtn.setUserData(text+ ";" + pF);
    }
    
    //public void setCheckListController(PassFailControlController controller) { passFailController = controller; }
}
