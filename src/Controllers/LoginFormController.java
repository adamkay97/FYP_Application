package Controllers;

import Classes.User;
import Managers.AuthenticationManager;
import Managers.DatabaseManager;
import Enums.ButtonTypeEnum;
import Managers.LanguageManager;
import Managers.RobotManager;
import Managers.SettingsManager;
import Managers.StageManager;
import com.jfoenix.controls.JFXCheckBox;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXPasswordField;
import javafx.application.Platform;
import javafx.event.ActionEvent;
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
    @FXML private JFXCheckBox chkRememberMe;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {
        User user = StageManager.getCurrentUser();
        if(user != null)
        {
            DatabaseManager dbManager = new DatabaseManager();
            
            if(dbManager.connect())
            {
                int id = dbManager.checkForRememberedUser();
                
                if(id == user.getUserId())
                {
                    txtUsername.setText(user.getUsername());
                    chkRememberMe.setSelected(true);
                }
            }
        }
        
        Platform.runLater(() -> {
            LanguageManager.setFormText("Login", mainAnchorPane.getScene());
        });
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
        DatabaseManager dbManager = new DatabaseManager();
        boolean rememberMe = chkRememberMe.isSelected();
        
        if(authManager.authenticateLogin(txtUsername.getText(), txtPassword.getText()))
        {
            //If login successful load user settings and open main form
            loadUserSettings();    
            
            if(dbManager.connect())
            {
                int userId = StageManager.getCurrentUser().getUserId();
                dbManager.rememberUser(userId, rememberMe);
                dbManager.disconnect();
            }
            
            StageManager.loadForm(StageManager.MAIN, new Stage());
            
            if(SettingsManager.getUsesNaoRobot())
            {
                //Run the robot connection on a seperate thread using the settings manager
                //after its been set above
                Thread connectThread = new Thread(() -> {
                    RobotManager.connectToRobot(SettingsManager.getRobotConnection());
                });
                connectThread.start();
            }
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
