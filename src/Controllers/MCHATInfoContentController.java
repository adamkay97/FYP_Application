package Controllers;

import Classes.DatabaseManager;
import Classes.StageManager;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.VBox;
import javafx.scene.text.*;

public class MCHATInfoContentController implements Initializable 
{
    @FXML VBox vboxInfoContent;

    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {
        DatabaseManager dbManager = new DatabaseManager();
        ArrayList<String> mchatInfo = new ArrayList<>();
        
        if(dbManager.connect())
        {
            mchatInfo = dbManager.loadInformationData(1, "MCHAT");
            dbManager.disconnect();
        }
        setVboxInformation(mchatInfo);
    }
    
    public void btnBack_Action(ActionEvent event)
    {
        StageManager.loadContentScene(StageManager.MAININFO);
    }
    
    /**
     * Writes the saved information in the db about the M-CHAT-R/F to the
     * page, setting it out in text flows within a vbox
     * @param mchatInfo List of separate paragraphs got from the db
     */
    private void setVboxInformation(ArrayList<String> mchatInfo)
    {
        vboxInfoContent.setSpacing(20);

        mchatInfo.stream().forEach((String info) -> 
        {
            String[] splitInfo = info.split("%");
            TextFlow tf = new TextFlow();
            
            if(splitInfo[0].equals("Link"))
            {
                Hyperlink link = createHyperlink(splitInfo[1]);
                vboxInfoContent.getChildren().add(link);
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
    
    private Hyperlink createHyperlink(String url)
    {
        Hyperlink mchatLink = new Hyperlink();
        mchatLink.setText(url);
        mchatLink.setFont(Font.font("Berlin Sans FB", FontWeight.NORMAL, 19));
        
        //Creates action for click of Hyperlink
        //Checks to see if the current machine supports this class
        mchatLink.setOnAction((ActionEvent e) -> {
            if(Desktop.isDesktopSupported())
            {
                //if it does uses the desktop class to launch the URL on the current pc's native URL launcher
                try {
                    Desktop.getDesktop().browse(new URI(url));
                } catch (IOException | URISyntaxException ex) {
                    System.out.println("Failed when loading MCHAT link - " + ex.getMessage());
                }
            }
        });
        
        return mchatLink;
    }
}
