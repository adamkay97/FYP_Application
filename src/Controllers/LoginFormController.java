/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers;

import Managers.AuthenticationManager;
import Managers.DatabaseManager;
import Enums.ButtonTypeEnum;
import Managers.StageManager;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXPasswordField;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class LoginFormController implements Initializable 
{
    @FXML private AnchorPane mainAnchorPane;
    @FXML private JFXTextField txtUsername;
    @FXML private JFXPasswordField txtPassword;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {
    } 
    
    @FXML private void btnLogin_Action(ActionEvent event)
    {
        loginProcess();
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
    
    public void setKeyboardListener()
    {
        Scene scene = mainAnchorPane.getScene();
        
        //Add Keyboard listener event for pressing enter instead of clicking button.
        scene.addEventFilter(KeyEvent.KEY_PRESSED, (KeyEvent event) -> {    
            if(event.getCode() == KeyCode.ENTER)
            {
                loginProcess();
                event.consume();
            }
        });
    }
    
    private void loginProcess()
    {
        //Check that username and password fields arent empty
        if(txtUsername.getText().equals("") || txtPassword.getText().equals(""))
            StageManager.loadPopupMessage("Warning", "Please enter your username and password. "
                    + "If you don't have an account please use the register page.", ButtonTypeEnum.OK);
        else
            attemptLogin();
    }
    
    private void attemptLogin()
    {
        //Creates instance of authentication manager to authenticate the login
        AuthenticationManager authManager = new AuthenticationManager();
        
        if(authManager.authenticateLogin(txtUsername.getText(), txtPassword.getText()))
        {
            //If login successful load user settings and open main form
            loadUserSettings();    
            StageManager.loadForm(StageManager.MAIN, new Stage());
            closeForm();
        }
    }
    
    private void loadUserSettings()
    {
        DatabaseManager dbManager = new DatabaseManager();
        
        if(dbManager.connect())
        {
            int userId = StageManager.getCurrentUser().getUserId();
            dbManager.loadApplicationSettings(userId);
            dbManager.disconnect();
        }
    }
    
    private void closeForm()
    {
        Stage currentStage = (Stage)mainAnchorPane.getScene().getWindow();
        currentStage.close();
    }
}
