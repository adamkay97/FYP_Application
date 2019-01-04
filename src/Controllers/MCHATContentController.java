package Controllers;

import Classes.DatabaseManager;
import Classes.StageManager;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.*;

public class MCHATContentController implements Initializable 
{
    @FXML VBox vboxInfoContent;

    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {
        DatabaseManager dbManager = new DatabaseManager();
        ArrayList<String> mchatInfo = new ArrayList<>();
        
        if(dbManager.connect())
        {
            mchatInfo = dbManager.loadMCHATInformation();
            dbManager.disconnect();
            
            setVboxInformation(mchatInfo);
        }
    }
    
    private void setVboxInformation(ArrayList<String> mchatInfo)
    {
        vboxInfoContent.setSpacing(20);

        mchatInfo.stream().forEach((String info) -> 
        {
            String[] splitInfo = info.split("%");
            TextFlow tf = new TextFlow();
            
            if(splitInfo[0].equals("Link"))
            {
                Hyperlink mchatLink = new Hyperlink();
                mchatLink.setText(splitInfo[1]);
                mchatLink.setFont(Font.font("Berlin Sans FB", FontWeight.NORMAL, 19));
                vboxInfoContent.getChildren().add(mchatLink);
                return;
            }
            else if(!splitInfo[0].equals(""))
            {
                Text header = new Text(splitInfo[0] + "\n");
                header.setFont(Font.font("Berlin Sans FB", FontWeight.BOLD, 20));
                header.setUnderline(true);
                tf.getChildren().add(header);
            }
            
            Text t = new Text(splitInfo[1]);
            t.setFont(Font.font("Berlin Sans FB", FontWeight.NORMAL, 19));
            tf.setTextAlignment(TextAlignment.JUSTIFY);
            tf.getChildren().add(t);
            vboxInfoContent.getChildren().add(tf);
        });
   }
    
    public void btnBack_Action(ActionEvent event)
    {
        StageManager.loadContentScene(StageManager.MAININFO);
    }
}
