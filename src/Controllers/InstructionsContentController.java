package Controllers;

import Managers.DatabaseManager;
import Managers.StageManager;
import Classes.FormTextLoader;
import Enums.ButtonTypeEnum;
import Managers.RobotManager;
import Managers.SettingsManager;
import com.jfoenix.controls.JFXCheckBox;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class InstructionsContentController implements Initializable 
{
    @FXML private VBox vboxInstructionsContent;
    private JFXCheckBox readInstructions;

    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {
        DatabaseManager dbManager = new DatabaseManager();
        FormTextLoader textLoader = new FormTextLoader(vboxInstructionsContent);
        ArrayList<String> instrInfo = new ArrayList<>();
        
        if(dbManager.connect())
        {
            instrInfo = dbManager.loadInformationData(1, "INSTRUCTIONS");
            dbManager.disconnect();
        }
        textLoader.setTextVboxInformation(instrInfo);
        
        //Once all of the text has been added to the VBox add a check box at the bottom
        //that must be selected before the user can proceed to ensure they have read the instructions.
        readInstructions = new JFXCheckBox("I have read and understood the instructions.");
        readInstructions.setFont(Font.font("Berlin Sans FB", FontWeight.NORMAL, 21));
        readInstructions.setTextFill(Color.GREEN);
        readInstructions.setCheckedColor(Color.GREEN);
        vboxInstructionsContent.getChildren().add(readInstructions);
    }    
    
    @FXML private void btnContinue_Action(ActionEvent event)
    {
        if(readInstructions.isSelected())
        {
            if(true)//RobotManager.getRobotConnected() || !SettingsManager.getUsesNaoRobot())
                StageManager.loadContentScene(StageManager.DETAILS);
            else
            {
                String msg = "There is currently no connection to the Robot. "
                        + "Please navigate to the settings page to setup the connection.";
                StageManager.loadPopupMessage("Warning", msg, ButtonTypeEnum.OK);
            }
        }
        else
        {
            String msg = "Please make sure you have read through all of the instructions before proceeding.";
            StageManager.loadPopupMessage("Warning", msg, ButtonTypeEnum.OK);
        }
    }
    
    @FXML private void btnInformation_Action(ActionEvent event)
    {
        StageManager.loadContentScene(StageManager.MAININFO);
        
        //Sets the underlined side menu button to be the info page
        StageManager.getMainFormController().setSelectedMenuButton("Info");
    }
}
