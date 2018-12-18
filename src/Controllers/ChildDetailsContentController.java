/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers;

import Classes.*;
import com.jfoenix.controls.*;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.control.*;
import javax.swing.JOptionPane;

public class ChildDetailsContentController implements Initializable {

    @FXML private JFXRadioButton radBtnFemale;
    @FXML private JFXRadioButton radBtnMale;
    @FXML private Button btnStartQuestions;
    @FXML private JFXTextField txtChildsAge;
    @FXML private JFXTextField txtChildsName;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML private void btnStartQuestions_Action(ActionEvent event)
    {
        if(ValidateUserInput())
        {
            StageManager.loadContentScene(StageManager.QUESTIONAIRE);
        }
        else
        {
            String messageText = "One or more fields have been left empty.\n"
                    + "All the fields must be filled in before you can proceed.";
            
            StageManager.loadPopupMessage("Error", messageText, ButtonTypeEnum.OK);
        }

    }
    
    private boolean ValidateUserInput()
    {
        if(txtChildsName.getText().equals(""))
            return false;
        
        if(txtChildsAge.getText().equals(""))
            return false;
        
        if(!radBtnMale.isSelected() && !radBtnFemale.isSelected())
            return false;
        
        return true;
    }
}
