package Controllers;

import Managers.RobotManager;
import Managers.SettingsManager;
import Managers.LanguageManager;
import Managers.StageManager;
import Enums.ButtonTypeEnum;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

public class SettingsContentController implements Initializable 
{
    @FXML private AnchorPane mainAnchorPane;
    
    @FXML private ToggleGroup useNaoGroup;
    @FXML private JFXRadioButton radBtnNaoYes;
    @FXML private JFXRadioButton radBtnNaoNo;
    
    @FXML private JFXTextField txtIPAddress;
    @FXML private JFXTextField txtFixedPort;
    @FXML private Label lblConnectStatus;
    @FXML private ImageView imgViewStatusIcon;
    @FXML private Button btnTestConnection;
    
    @FXML private JFXComboBox cmbBoxLanguage;
    @FXML private ToggleGroup noteMethodGroup;
    @FXML private JFXRadioButton radBtnText;
    @FXML private JFXRadioButton radBtnAudio;
    @FXML private JFXTextField txtAudioFileLocation;
    
    private boolean resetLanguage = false;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {
        setupSettingsOptions();
        LanguageManager.setFormText("Settings", StageManager.getRootScene());
        
        /*Platform.runLater(() ->{
            LanguageManager.setFormText("Settings", mainAnchorPane.getScene());
        });*/
    }  
    
    @FXML public void btnSave_Action(ActionEvent event) 
    {
        saveSettingsProcess();
    }
    
    @FXML public void btnTestConnection_Action(ActionEvent event)
    {
        //Set cursor so user knows the application is doing something
        mainAnchorPane.setCursor(javafx.scene.Cursor.WAIT);
        
        String ip = txtIPAddress.getText();
        String port = txtFixedPort.getText();
        
        if(validateDetails(ip, port))
        {
            //Attempts connection once other FXML threads have finished
            Platform.runLater(() -> {
                attemptConnection(ip, port);        
            });
        }
        else
        {
            String msg = "Incorrect format. Please check you have entered the correct IP address and Port numbers.";
            StageManager.loadPopupMessage("Warning", msg, ButtonTypeEnum.OK);
            mainAnchorPane.setCursor(javafx.scene.Cursor.DEFAULT);
        }   
    }
    
    @FXML public void btnConnectionHelp_Action(ActionEvent event) 
    {
        String msg = "To find out the Robots URL address, press the middle button on its chest. This will also tell you if "
                    + "the robot has connected to the network yet (this may take some time).";
        StageManager.loadPopupMessage("Information", msg, ButtonTypeEnum.OK);
    }
    
    private void attemptConnection(String ip, String port)
    {
        String url = String.format("tcp://%s:%s", ip, port);

        boolean connected = RobotManager.connectToRobot(url);
        setConnectedImage(connected);

        mainAnchorPane.setCursor(javafx.scene.Cursor.DEFAULT);

        if(connected)
        {
            String msg = "Successfully connected to NAO.";
            StageManager.loadPopupMessage("Success", msg, ButtonTypeEnum.OK);
        }
        else
        {
            setConnectedImage(false);

            String msg = "Could not connect to NAO. Please verify the IP address, "
                    + "Port Number and make sure NAO is connected to your network";
            StageManager.loadPopupMessage("Error", msg, ButtonTypeEnum.OK);
        }  
    }
    
    private void saveSettingsProcess()
    {
        boolean save = true;
        boolean usesNAO = radBtnNaoYes.isSelected();
        
        if(!RobotManager.getRobotConnected() && usesNAO)
        {
            //If the robot is not connected Pop up a message checking the user 
            //still wants to save these ip and port settings
            String msg = "The Robot does not seem to be connected. Have you tested the "
                    + "connection and still wish to save these details?";
            
            if(!StageManager.loadPopupMessage("Warning", msg, ButtonTypeEnum.YESNO))
                save = false;
        }
        
        if(save)
        {
            //If settings are to be saved then set the values on the Settings Manager
            //Then call saveCurrentSettings to store the data in the db;
            String ip = txtIPAddress.getText();
            String port = txtFixedPort.getText();
            
            String url = String.format("tcp://%s:%s", ip, port);
            String noteMethod = radBtnText.isSelected() ? "TextArea" : "Audio";
            String language = (String)cmbBoxLanguage.getValue();
            
            if(!language.equals(SettingsManager.getLanguage()))
                resetLanguage = true;
            
            if(noteMethod.equals("Audio"))
                SettingsManager.setAudioFileLocation(txtAudioFileLocation.getText());
            
            SettingsManager.setUsesNaoRobot(usesNAO);
            SettingsManager.setRobotConnection(url);
            SettingsManager.setRobotVolume(50);
            SettingsManager.setNoteMethod(noteMethod);
            SettingsManager.setLanguage(language);
            SettingsManager.saveCurrentSettings();
            
            String msg = "Success. Your settings have been saved.";
            StageManager.loadPopupMessage("Information", msg, ButtonTypeEnum.OK);
            
            if(resetLanguage)
                resetFormLanguage(language);
        }
    }
    
    private void setupSettingsOptions()
    {
        cmbBoxLanguage.getItems().addAll("English", "Italian");
        cmbBoxLanguage.getSelectionModel().select(SettingsManager.getLanguage());
        cmbBoxLanguage.setStyle("-fx-font: 20px \"Berlin Sans FB\";");
        
        boolean usesNAO = SettingsManager.getUsesNaoRobot();
        String robotURL = SettingsManager.getRobotConnection();
        //int volume = SettingsManager.getRobotVolume();
        String noteMethod = SettingsManager.getNoteMethod();
        String audioPath = SettingsManager.getAudioFileLocation();
        
        //Get the robot connection from the SettingsManager
        //Remove the 'tcp://' from the start and split on ':' to get Ip and Fixed Port
        robotURL = robotURL.substring(6);
        String[] urlSplit = robotURL.split(":");
        
        txtIPAddress.setText(urlSplit[0]);
        txtFixedPort.setText(urlSplit[1]);
        setConnectedImage(RobotManager.getRobotConnected());
        //sliderVolume.setValue(volume);
        
        if(!usesNAO)
        {
            radBtnNaoNo.setSelected(true);
            setNAOControls(true);
        }
        
        if(noteMethod.equals("TextArea"))
        {
            radBtnText.setSelected(true);
            txtAudioFileLocation.setDisable(true);
        }
        else
        {
            radBtnAudio.setSelected(true);
            txtAudioFileLocation.setDisable(false);
        }
        
        //If the audio path is empty give the default path for where to save the audio files
        if(audioPath.equals(""))
        {
            String username = StageManager.getCurrentUser().getUsername();
            String currentDirectory = System.getProperty("user.dir");
            txtAudioFileLocation.setText(String.format("%s\\AudioFiles\\%s\\", currentDirectory, username));
        }
        else
            txtAudioFileLocation.setText(audioPath);
        
        //Create radio button listeners for UsesNao and UsesAudio
        createRadioButtonListeners();
    }
    
    private void setConnectedImage(boolean connected)
    {
        Image connectIcon;
        
        //Depending if the robot is connected set the ImageView to show 
        //corresponding connected icon
        if(connected)
        {
            connectIcon = new Image("Icons/connected.png");
            imgViewStatusIcon.setImage(connectIcon);
            lblConnectStatus.setText("Connected");
        }
        else
        {
            connectIcon = new Image("Icons/disconnected.png");
            imgViewStatusIcon.setImage(connectIcon);
            lblConnectStatus.setText("Disconnected");
        }
    }
    
    private void setNAOControls(boolean disable)
    {
        txtIPAddress.setDisable(disable);
        txtFixedPort.setDisable(disable);
        lblConnectStatus.setDisable(disable);
        imgViewStatusIcon.setVisible(!disable);
        btnTestConnection.setDisable(disable);
        //sliderVolume.setDisable(disable);
    }
    
    private void createRadioButtonListeners()
    {
        //Add listener to NoteToggleGroup to enable/disable the file path text field 
        noteMethodGroup.selectedToggleProperty().addListener(
            (ObservableValue<? extends Toggle> ov, Toggle old_toggle, Toggle new_toggle) -> 
            {
                if (noteMethodGroup.getSelectedToggle() != null) 
                {
                    if(noteMethodGroup.getSelectedToggle().equals(radBtnAudio))
                        txtAudioFileLocation.setDisable(false);
                    else
                        txtAudioFileLocation.setDisable(true);
                } 
            });
        
        //Add listener to UseNAO Toggle Group to enable or disable all NAO controls
        useNaoGroup.selectedToggleProperty().addListener(
            (ObservableValue<? extends Toggle> ov, Toggle old_toggle, Toggle new_toggle) -> 
            {
                if (useNaoGroup.getSelectedToggle() != null) 
                {
                    if(useNaoGroup.getSelectedToggle().equals(radBtnNaoYes))
                        setNAOControls(false);
                    else
                        setNAOControls(true);
                } 
            });
    }
    
    private void resetFormLanguage(String language)
    {
        LanguageManager.setLanguage(language);
        LanguageManager.loadFormText();
        
        LanguageManager.setFormText("Main", StageManager.getRootScene());
        LanguageManager.setFormText("Settings", StageManager.getRootScene());
    }
    
    private boolean validateDetails(String ip, String port)
    {
        //Validate that each part of the IP is a number and 
        //also that the port is a number
        String[] ipNumbers = ip.split("\\.");
        for(String num : ipNumbers)
        {
            if(!isNumeric(num))
                return false; 
        }
        
        return isNumeric(port);
    }
        
    
    private boolean isNumeric(String value) 
    {
        //Parse user input to see if the age input is numeric or not
        try {
            int i = Integer.parseInt(value);
        } 
        catch (NumberFormatException | NullPointerException ex) {
            return false;
        }
        return true;
    }
}
