package Controllers;

import Classes.RobotManager;
import Classes.SettingsManager;
import Classes.StageManager;
import Enums.ButtonTypeEnum;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXTextField;
import java.awt.Cursor;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

public class SettingsContentController implements Initializable 
{
    @FXML private AnchorPane mainAnchorPane;
    @FXML private JFXTextField txtIPAddress;
    @FXML private JFXTextField txtFixedPort;
    @FXML private Label lblConnectStatus;
    @FXML private ImageView imgViewStatusIcon;
    
    @FXML private JFXSlider sliderVolume;
    
    @FXML private JFXRadioButton radBtnText;
    @FXML private JFXRadioButton radBtnAudio;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {
        String robotURL = SettingsManager.getRobotConnection();
        int volume = SettingsManager.getRobotVolume();
        String noteMethod = SettingsManager.getNoteMethod();
        
        //Get the robot connection from the SettingsManager
        //Remove the 'tcp://' from the start and split on ':' to get Ip and Fixed Port
        robotURL = robotURL.substring(6);
        String[] urlSplit = robotURL.split(":");
        
        txtIPAddress.setText(urlSplit[0]);
        txtFixedPort.setText(urlSplit[1]);
        
        setConnectedImage(RobotManager.getRobotConnected());
        
        sliderVolume.setValue(volume);
        if(noteMethod.equals("TextArea"))
            radBtnText.setSelected(true);
        else
            radBtnAudio.setSelected(true);
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
        if(!RobotManager.getRobotConnected())
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
            String ip = txtIPAddress.getText();
            String port = txtFixedPort.getText();
            
            String url = String.format("tcp://%s:%s", ip, port);
            int volume = (int) sliderVolume.getValue();
            String noteMethod = radBtnText.isSelected() ? "TextArea" : "Audio";
            
            SettingsManager.setRobotConnection(url);
            SettingsManager.setRobotVolume(volume);
            SettingsManager.setNoteMethod(noteMethod);
            SettingsManager.saveCurrentSettings();
        }
    }
    
    private void setConnectedImage(boolean connected)
    {
        Image connectIcon;
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
