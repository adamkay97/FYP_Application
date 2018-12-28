/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers;

import Classes.AuthenticationManager;
import Classes.ButtonTypeEnum;
import Classes.StageManager;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXPasswordField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class LoginFormController implements Initializable 
{
    @FXML private AnchorPane mainAnchorPane;
    @FXML private JFXTextField txtUsername;
    @FXML private JFXPasswordField txtPassword;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    } 
    
    @FXML private void btnLogin_Action(ActionEvent event)
    {
        //Check that username and password fields arent empty
        if(txtUsername.getText().equals("") || txtPassword.getText().equals(""))
        {
            StageManager.loadPopupMessage("Warning", "Please enter your username and password. "
                    + "If you don't have an account please use the register page.", ButtonTypeEnum.OK);
        }
        else
        {
            attemptLogin();
        }
    }
    
    @FXML private void btnRegister_Action(ActionEvent event)
    {
        //Load register form
        StageManager.loadForm(StageManager.REGISTER, new Stage());
    }
    
    @FXML private void btnExit_Action(ActionEvent event) 
    {
        closeForm();
    }
    
    private void attemptLogin()
    {
        //Creates instance of authentication manager to authenticate the login
        AuthenticationManager authManager = new AuthenticationManager();
        
        if(authManager.authenticateLogin(txtUsername.getText(), txtPassword.getText()))
        {
            //If login successful open main form
            StageManager.loadForm(StageManager.MAIN, new Stage());
            closeForm();
        }
    }
    
    private void closeForm()
    {
        Stage currentStage = (Stage)mainAnchorPane.getScene().getWindow();
        currentStage.close();
    }
}
