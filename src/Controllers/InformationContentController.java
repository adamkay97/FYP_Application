package Controllers;

import Classes.DatabaseManager;
import Classes.StageManager;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;

public class InformationContentController implements Initializable 
{
    @FXML VBox vboxMainInfoContent;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {
        DatabaseManager dbManager = new DatabaseManager();
        ArrayList<String> mainInfo = new ArrayList<>();
        
        if(dbManager.connect())
        {
            mainInfo = dbManager.loadInformationData(1, "MAIN");
            dbManager.disconnect();
        }
        setVboxInformation(mainInfo);
    }    
    
    @FXML public void btnMCHAT_Action(ActionEvent event)
    {
        StageManager.loadContentScene(StageManager.MCHATINFO);
        //Closes the menu drawer on the main form when the information btn is clicked
        StageManager.getMainFormController().closeMenuDrawer();
    }
    
    private void setVboxInformation(ArrayList<String> mainInfo)
    {
        vboxMainInfoContent.setSpacing(20);

        mainInfo.stream().forEach((String info) -> 
        {
            String[] splitInfo = info.split("%");
            TextFlow tf = new TextFlow();
            
            Text t = new Text(splitInfo[1]);
            t.setFont(Font.font("Berlin Sans FB", FontWeight.NORMAL, 21));
            tf.setTextAlignment(TextAlignment.CENTER);
            tf.getChildren().add(t);
            vboxMainInfoContent.getChildren().add(tf);
        });
   }
}
