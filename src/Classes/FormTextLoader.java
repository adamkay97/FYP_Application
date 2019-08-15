package Classes;

import Managers.LogManager;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import javafx.event.ActionEvent;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;

public class FormTextLoader 
{
    private final VBox vboxInfoContent;
    private final LogManager logManager = new LogManager();
    
    public FormTextLoader(VBox vbox)
    {
        vboxInfoContent = vbox;
    }
    
    /**
     * Writes the saved information in the db to the page, setting it
     * out in text flows within a VBox. Just for text
     * @param textData List of separate paragraphs got from the db
     */
    public void setTextVboxInformation(ArrayList<String> textData)
    {
        vboxInfoContent.setSpacing(20);

        textData.stream().forEach((String info) -> 
        {
            String[] splitInfo = info.split("%");
            TextFlow tf = new TextFlow();
            
            Text t = new Text(splitInfo[1]);
            t.setFont(Font.font("Berlin Sans FB", FontWeight.NORMAL, 21));
            tf.setTextAlignment(TextAlignment.CENTER);
            tf.getChildren().add(t);
            vboxInfoContent.getChildren().add(tf);
        });
    }
    
    /**
     * Writes the saved information in the db to the page, setting it
     * out in text flows within a VBox, has functionality for adding web links
     * @param textData List of separate paragraphs got from the db
     */
    public void setAllVboxInformation(ArrayList<String> textData)
    {
        vboxInfoContent.setSpacing(20);

        textData.stream().forEach((String info) -> 
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
                    logManager.ErrorLog("Failed when loading MCHAT link - " + ex.getMessage());
                }
            }
        });
        
        return mchatLink;
    }
}
