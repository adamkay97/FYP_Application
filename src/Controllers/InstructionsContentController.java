package Controllers;

import Classes.DatabaseManager;
import Classes.StageManager;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;

public class InstructionsContentController implements Initializable 
{
    @FXML private VBox vboxInstructionsContent;

    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {
        DatabaseManager dbManager = new DatabaseManager();
        ArrayList<String> instrInfo = new ArrayList<>();
        
        if(dbManager.connect())
        {
            instrInfo = dbManager.loadInformationData(1, "INSTRUCTIONS");
            dbManager.disconnect();
        }
        setVboxInformation(instrInfo);
    }    
    
    @FXML private void btnContinue_Action(ActionEvent event)
    {
        StageManager.loadContentScene(StageManager.DETAILS);
    }
    
    @FXML private void btnInformation_Action(ActionEvent event)
    {
        StageManager.loadContentScene(StageManager.MAININFO);
        
        //Sets the underlined side menu button to be the info page
        StageManager.getMainFormController().setSelectedMenuButton("Info");
    }
    
    private void setVboxInformation(ArrayList<String> mainInfo)
    {
        vboxInstructionsContent.setSpacing(20);

        mainInfo.stream().forEach((String info) -> 
        {
            String[] splitInfo = info.split("%");
            TextFlow tf = new TextFlow();
            
            Text t = new Text(splitInfo[1]);
            t.setFont(Font.font("Berlin Sans FB", FontWeight.NORMAL, 21));
            tf.setTextAlignment(TextAlignment.CENTER);
            tf.getChildren().add(t);
            vboxInstructionsContent.getChildren().add(tf);
        });
   }
}
