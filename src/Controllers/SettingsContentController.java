package Controllers;

import Classes.PopupText;
import Managers.RobotManager;
import Managers.SettingsManager;
import Managers.LanguageManager;
import Managers.StageManager;
import Enums.ButtonTypeEnum;
import Managers.DatabaseManager;
import Managers.QuestionaireManager;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

public class SettingsContentController implements Initializable 
{
    @FXML private AnchorPane mainAnchorPane;
    @FXML private ScrollPane scrlPnSettings;
    @FXML private VBox vboxSettings;
    
    @FXML private ToggleGroup useNaoGroup;
    @FXML private JFXRadioButton radBtnNaoYes;
    @FXML private JFXRadioButton radBtnNaoNo;
    
    @FXML private JFXTextField txtIPAddress;
    @FXML private JFXTextField txtFixedPort;
    @FXML private Label lblConnectStatus;
    @FXML private ImageView imgViewStatusIcon;
    @FXML private Button btnTestConnection;
    
    @FXML private JFXComboBox cmbBoxLanguage;
    
    @FXML private JFXComboBox cmbBoxQuestionSet;
    @FXML private JFXComboBox cmbBoxSetLanguage;
    @FXML private ToggleGroup noteMethodGroup;
    @FXML private JFXRadioButton radBtnText;
    @FXML private JFXRadioButton radBtnAudio;
    @FXML private JFXTextField txtAudioFileLocation;
    
    private boolean resetLanguage = false;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {
        setupSettingsOptions();
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
            PopupText popup = LanguageManager.getPopupText(23);
            StageManager.loadPopupMessage(popup.getHeader(), popup.getMessage(), ButtonTypeEnum.OK);
            mainAnchorPane.setCursor(javafx.scene.Cursor.DEFAULT);
        }   
    }
    
    @FXML public void btnConnectionHelp_Action(ActionEvent event) 
    {
        PopupText popup = LanguageManager.getPopupText(24);
        StageManager.loadPopupMessage(popup.getHeader(), popup.getMessage(), ButtonTypeEnum.OK);
    }
    
    private void attemptConnection(String ip, String port)
    {
        String url = String.format("tcp://%s:%s", ip, port);

        boolean connected = RobotManager.connectToRobot(url);
        setConnectedImage(connected);

        mainAnchorPane.setCursor(javafx.scene.Cursor.DEFAULT);

        if(connected)
        {
            PopupText popup = LanguageManager.getPopupText(25);
            StageManager.loadPopupMessage(popup.getHeader(), popup.getMessage(), ButtonTypeEnum.OK);
        }
        else
        {
            setConnectedImage(false);

            PopupText popup = LanguageManager.getPopupText(26);
            StageManager.loadPopupMessage(popup.getHeader(), popup.getMessage(), ButtonTypeEnum.OK);
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
            PopupText popup = LanguageManager.getPopupText(27);
            
            if(!StageManager.loadPopupMessage(popup.getHeader(), popup.getMessage(), ButtonTypeEnum.YESNO))
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
            
            String questionSet = (String)cmbBoxQuestionSet.getValue();
            String setLanguage = (String)cmbBoxSetLanguage.getValue();
            
            if(!language.equals(SettingsManager.getLanguage()))
                resetLanguage = true;
            
            if(noteMethod.equals("Audio"))
                SettingsManager.setAudioFileLocation(txtAudioFileLocation.getText());
            
            SettingsManager.setUsesNaoRobot(usesNAO);
            SettingsManager.setRobotConnection(url);
            SettingsManager.setQuestionSet(questionSet);
            SettingsManager.setSetLanguage(setLanguage);
            SettingsManager.setNoteMethod(noteMethod);
            SettingsManager.setLanguage(language);
            
            SettingsManager.saveCurrentSettings();
            
            PopupText popup = LanguageManager.getPopupText(28);
            StageManager.loadPopupMessage(popup.getHeader(), popup.getMessage(), ButtonTypeEnum.OK);
            
            if(resetLanguage)
                resetFormLanguage(language);
        }
    }
    
    private void setupSettingsOptions()
    {
        //Bind vbox dimensions to scroll pane for maximise scene
        vboxSettings.prefWidthProperty().bind(scrlPnSettings.widthProperty());
        
        //Set contents and styles of combo boxes
        cmbBoxLanguage.getItems().addAll(LanguageManager.getLanguageList());
        cmbBoxLanguage.getSelectionModel().select(SettingsManager.getLanguage());
        cmbBoxLanguage.setStyle("-fx-font: 20px \"Berlin Sans FB\";");
        
        cmbBoxQuestionSet.getItems().addAll(QuestionaireManager.getQuestionSets());
        cmbBoxQuestionSet.getSelectionModel().select(SettingsManager.getQuestionSet());
        cmbBoxQuestionSet.setStyle("-fx-font: 20px \"Berlin Sans FB\";");
        
        cmbBoxSetLanguage.setStyle("-fx-font: 20px \"Berlin Sans FB\";");
        updateSetLanguageBox(SettingsManager.getQuestionSet());
        cmbBoxSetLanguage.getSelectionModel().select(SettingsManager.getSetLanguage());
        
        boolean usesNAO = SettingsManager.getUsesNaoRobot();
        String robotURL = SettingsManager.getRobotConnection();
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
        
        //Create input listeners for UsesNao, UsesAudio and Question Set ComboBox 
        createFormInputListeners();
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
            lblConnectStatus.setId("S9.1");
        }
        else
        {
            connectIcon = new Image("Icons/disconnected.png");
            imgViewStatusIcon.setImage(connectIcon);
            lblConnectStatus.setId("S9.2");
        }
        LanguageManager.setLabelText("Settings", lblConnectStatus);
    }
    
    private void setNAOControls(boolean disable)
    {
        txtIPAddress.setDisable(disable);
        txtFixedPort.setDisable(disable);
        lblConnectStatus.setDisable(disable);
        imgViewStatusIcon.setVisible(!disable);
        btnTestConnection.setDisable(disable);
    }
    
    private void createFormInputListeners()
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
        
        
        //If combobox value changed reset the question set language items.
        cmbBoxQuestionSet.valueProperty().addListener(new ChangeListener<String>() {
            @Override public void changed(ObservableValue ov, String oldSet, String newSet) 
            {
                if(newSet != null)
                    updateSetLanguageBox(newSet);
            }    
        });
    }
    
    private void updateSetLanguageBox(String setName)
    {
        DatabaseManager dbManager = new DatabaseManager();
        if(dbManager.connect())
        {
            cmbBoxSetLanguage.getItems().setAll(dbManager.getQuestionSetLanguages(setName));
            dbManager.disconnect();
        }
    }
    
    private void resetFormLanguage(String language)
    {
        //Reset language, load form text and reset current scenes
        LanguageManager.setLanguage(language);
        LanguageManager.loadFormText();
        
        LanguageManager.setFormText("Main", StageManager.getRootScene());
        LanguageManager.setFormText("Settings", StageManager.getRootScene());
        
        //Reload the question lists with the new language
        DatabaseManager dbManager = new DatabaseManager();
        if(dbManager.connect())
        {
            dbManager.loadQuestionList(SettingsManager.getQuestionSet());
            dbManager.loadFollowUpList();
        }
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
