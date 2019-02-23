package Controllers;

import Classes.ReviewData;
import Classes.StageManager;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;

public class QuestionReviewContentController implements Initializable 
{
    @FXML private Label lblQuestionHeader; 
    @FXML private VBox vboxInfoContent;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {}
    
    public void btnBack_Action(ActionEvent event) throws IOException
    {
        //Uses the variable set on the StageManager to easily reload the previous scene
        StageManager.loadContentSceneParent(StageManager.getCurrentChildReviewParent());
    }
    
    public void setupQuestionReviewContent(ReviewData reviewData)
    {   
        //Split questionText so that only the question is set on the label and not the example
        String questionText = reviewData.getQuestionText().split("\n")[0];
        String header = Integer.toString(reviewData.getQuestionNumber()) + " : " + questionText;
        lblQuestionHeader.setText(header);
        
        vboxInfoContent.setSpacing(20);
        
        String notes = reviewData.getQuestionNotes().equals("") ? 
                        "No notes given." : reviewData.getQuestionNotes();
        
        displayReviewData("Question Answer:", reviewData.getQuestionAnswer(), false);
        displayReviewData("Notes:", notes, false);
        displayReviewData("Follow Up Result:", reviewData.getFollowUpResult(), false);
        displayReviewData("Follow Up Questions/Answers:", reviewData.getFollowUpText(), true);
    }
    
    private void displayReviewData(String headerText, String data, boolean followUp)
    {
        TextFlow tf = new TextFlow();
        Text answer;
        
        Text header = new Text(headerText + "\n");
        header.setFont(Font.font("Berlin Sans FB", FontWeight.BOLD, 20));
        header.setUnderline(true);
        tf.getChildren().add(header);
        
        if(followUp)
        {
            String[] followUpText = data.split("%");
            
            for(String text : followUpText)
            {
                if(text.split(";").length > 1)
                {
                    String[] checklistText = text.split(";");
                    Text checkHeader = new Text("Example Checklist Answers:\n");
                    checkHeader.setFont(Font.font("Berlin Sans FB", FontWeight.NORMAL, 19));
                    checkHeader.setUnderline(true);
                    tf.getChildren().add(checkHeader);
                    
                    for(String check : checklistText)
                    {
                        answer = new Text(check + "\n");
                        answer.setFont(Font.font("Berlin Sans FB", FontWeight.NORMAL, 19));
                        tf.getChildren().add(answer);
                    }
                }
                else
                {
                    if(text.split("=").length > 1)
                    {
                        String[] split = text.split("=");
                        
                        Text checkHeader = new Text("\n" + split[0] + "\n");
                        checkHeader.setFont(Font.font("Berlin Sans FB", FontWeight.NORMAL, 19));
                        checkHeader.setUnderline(true);
                        tf.getChildren().add(checkHeader);
                        
                        answer = new Text(split[1] + "\n\n");
                        answer.setFont(Font.font("Berlin Sans FB", FontWeight.NORMAL, 19));
                        tf.getChildren().add(answer);
                        
                    }
                    else
                    {
                        answer = new Text(text + "\n\n");
                        answer.setFont(Font.font("Berlin Sans FB", FontWeight.NORMAL, 19));
                        tf.getChildren().add(answer);
                    }
                }
            }
            
        }
        else
        {
            answer = new Text(data);
            answer.setFont(Font.font("Berlin Sans FB", FontWeight.NORMAL, 19));
            tf.getChildren().add(answer);
        }
        tf.setTextAlignment(TextAlignment.JUSTIFY);
        
        vboxInfoContent.getChildren().add(tf);
    }
}