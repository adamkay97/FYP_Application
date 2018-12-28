package Controllers;

import Classes.*;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import java.util.HashMap;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterFormController implements Initializable 
{
    @FXML private AnchorPane mainAnchorPane;
    @FXML private JFXTextField txtUsername;
    @FXML private JFXPasswordField txtConfirmPassword;
    @FXML private JFXTextField txtLastName;
    @FXML private JFXTextField txtFirstName;
    @FXML private JFXPasswordField txtPassword;
    
    //Using regex, sets the pattern that must be matched by the input
    //in order to restrict what can be inputted into the database
    private final Pattern loginPattern = Pattern.compile("^[a-zA-Z0-9]{6,15}$");
    private final Pattern namePattern = Pattern.compile("^[a-zA-Z]{2,15}$");

    private boolean v1, v2, v3, v4, v5;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {
        v1 = false; v2 = false; v3 = false; v4 = false; v5 = false;
        
        //Adds listener using lambda expression for username text field, if when 
        //lost focus the username does not match validation rules turns the field red else it will turn green.
        txtUsername.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> 
        {
            if(!newValue)
            {
                Matcher inputMatcher = loginPattern.matcher(txtUsername.getText());
                if(inputMatcher.matches())
                {
                    //Check to see if that username already exist in the userMap
                    HashMap<String, User> userMap = DatabaseManager.getUserMap();
                    User currentUser = userMap.get(txtUsername.getText());
                    if(currentUser == null)
                        v1 = true;
                    else
                    {
                        StageManager.loadPopupMessage("Warning", "A user with this username already exists, "
                            + "please use a different username to proceed.", ButtonTypeEnum.OK);
                        v1 = false;
                    }
                }
                setTextFieldColor(txtUsername, v1);
            }
        });
        
        //Adds listener using lambda expression for password text field, if when 
        //lost focus the password does not match validation pattern and doesnt
        //contain at least 2 digits it turns the field red else it will turn green.
        txtPassword.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> 
        {
            if(!newValue)
            {
                int digitCount = 0;
                String password = txtPassword.getText();
                
                Matcher inputMatcher = loginPattern.matcher(password);
                if(inputMatcher.matches())
                {
                    for (int i = 0; i < password.length(); i++) {
                        if(Character.isDigit(password.charAt(i)))
                            digitCount++;
                    }
                    
                    v2 = digitCount >= 2;
                }
                
                setPasswordFieldColor(txtPassword, v2);
            }
        });
        
        //Adds listener using lambda expression for confirm password text field, if when lost focus 
        //the confirm password does not match the value in password it turns the field red else it will turn green.
        txtConfirmPassword.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> 
        {
            if(!newValue)
            {
                v3 = txtConfirmPassword.getText().equals(txtPassword.getText());
                setPasswordFieldColor(txtConfirmPassword, v3);
            }
        });
        
        //Adds listener using lambda expression for firstname text field, if when 
        //lost focus the first name does not match validation rules turns the field red else it will turn green.
        txtFirstName.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> 
        {
            if(!newValue)
            {
                Matcher inputMatcher = namePattern.matcher(txtFirstName.getText());
                v4 = inputMatcher.matches();
                setTextFieldColor(txtFirstName, v4);
            }
        });
        
        //Adds listener using lambda expression for last name text field, if when 
        //lost focus the last name does not match validation rules turns the field red else it will turn green.
        txtLastName.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> 
        {
            if(!newValue)
            {
                Matcher inputMatcher = namePattern.matcher(txtLastName.getText());
                v5 = inputMatcher.matches();
                setTextFieldColor(txtLastName, v5);
            }
        });
        
    }   
    
    @FXML private void btnSubmit_Action(ActionEvent event) 
    {
        //Makes sure all inputs are valid before using the input
        //to create a new user
        if(v1 && v2 && v3 && v4 && v5)
        {
            createNewUser();
            closeForm();
         }
    }
    
    @FXML private void btnQuit_Click(ActionEvent event) 
    {
        closeForm();
    }
    
    private void createNewUser()
    {
        AuthenticationManager authManager = new AuthenticationManager();
        
        //Sets the user id to the size of the current users plus 1.
        //Uses the instance of the AuthenticationManager to create a hashed password
        //so it can be written to the database
        int userId = DatabaseManager.getUserMap().size() + 1;
        String username = txtUsername.getText();
        String hashPassword = authManager.createPasswordHash(txtConfirmPassword.getText());
        String firstName = txtFirstName.getText();
        String lastName = txtLastName.getText();
        
        User newUser = new User(userId, username, hashPassword, firstName, lastName);
        
        //Call the DatabaseManager to write the new user to the database
        if(DatabaseManager.writeUserToDatabase(newUser))
        {
            StageManager.loadPopupMessage("Success", "You have successfully created a new account, "
                    + "please use your details to login.", ButtonTypeEnum.OK);
        }
        else
        {
            StageManager.loadPopupMessage("Error", "There was an issue with saving your information, "
                    + "please try again. If this error persists please contact support.", ButtonTypeEnum.OK);
        }
    }
    
    private void setTextFieldColor(JFXTextField txtField, boolean valid)
    {
        //Sets the text fields to red when the input is incorrect
        if(!valid)
            txtField.setStyle("-fx-text-fill: #FF2D00; -jfx-unfocus-color: #FF2D00;");
        else
            txtField.setStyle("-fx-text-fill: #000000; -jfx-unfocus-color: #696969;");
    }
    
    private void setPasswordFieldColor(JFXPasswordField passField, boolean valid)
    {
        //Sets the password fields to red when the input is incorrect
        if(!valid)
            passField.setStyle("-fx-text-fill: #FF2D00; -jfx-unfocus-color: #FF2D00;");
        else
            passField.setStyle("-fx-text-fill: #000000; -jfx-unfocus-color: #696969;");
    }
    
    private void closeForm()
    {
        Stage currentStage = (Stage)mainAnchorPane.getScene().getWindow();
        currentStage.close();
    }
}
 