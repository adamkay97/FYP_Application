package Controllers;

import Managers.DatabaseManager;
import Managers.StageManager;
import Classes.FormTextLoader;
import Classes.PopupText;
import Enums.ButtonTypeEnum;
import Managers.LanguageManager;
import Managers.RobotManager;
import Managers.SettingsManager;
import com.jfoenix.controls.JFXCheckBox;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;
import javafx.application.Platform;
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
            instrInfo = dbManager.loadInformationData("INSTRUCTIONS");
            dbManager.disconnect();
        }
        textLoader.setTextVboxInformation(instrInfo);
        
        Platform.runLater(() -> {addReadCheckbox();});
    }    
    
    @FXML private void btnContinue_Action(ActionEvent event)
    {
        if(readInstructions.isSelected())
        {
            if(RobotManager.getRobotConnected() || !SettingsManager.getUsesNaoRobot())
                StageManager.loadContentScene(StageManager.DETAILS);
            else
            {
                PopupText popup = LanguageManager.getPopupText(9);
                StageManager.loadPopupMessage(popup.getHeader(), popup.getMessage(), ButtonTypeEnum.OK);
            }
        }
        else
        {
            PopupText popup = LanguageManager.getPopupText(10);
            StageManager.loadPopupMessage(popup.getHeader(), popup.getMessage(), ButtonTypeEnum.OK);
        }
    }
    
    @FXML private void btnInformation_Action(ActionEvent event)
    {
        StageManager.loadContentScene(StageManager.MAININFO);
        
        //Sets the underlined side menu button to be the info page
        StageManager.getMainFormController().setSelectedMenuButton("Info");
    }
    
    private void addReadCheckbox()
    {
        HashMap<String, String> formText = LanguageManager.getSpecifiedText("Instructions");
        
        //Once all of the text has been added to the VBox add a check box at the bottom
        //that must be selected before the user can proceed to ensure they have read the instructions.
        readInstructions = new JFXCheckBox();
        readInstructions.setFont(Font.font("Berlin Sans FB", FontWeight.NORMAL, 21));
        readInstructions.setTextFill(Color.GREEN);
        readInstructions.setCheckedColor(Color.GREEN);
        readInstructions.setText(formText.get("I4"));
        
        vboxInstructionsContent.getChildren().add(readInstructions);
    }
}
