package Controllers;

import Classes.Child;
import Classes.PopupText;
import Classes.ReviewData;
import Enums.ButtonTypeEnum;
import Managers.LanguageManager;
import Managers.SettingsManager;
import Managers.StageManager;
import com.jfoenix.controls.JFXButton;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;

public class QuestionReviewContentController implements Initializable 
{
    @FXML private Label lblQuestionHeader; 
    @FXML private VBox vboxInfoContent;
    
    private ReviewData reviewData;
    private Child currentChild;
    
    private HashMap<String, String> reviewText;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {
        reviewText = LanguageManager.getSpecifiedText("ReviewText");
    }
    
    public void btnBack_Action(ActionEvent event) throws IOException
    {
        //Uses the variable set on the StageManager to easily reload the previous scene
        StageManager.loadContentSceneParent(StageManager.getCurrentChildReviewParent());
    }
    
    public void setupQuestionReviewContent(ReviewData data, Child child)
    {   
        reviewData = data;
        currentChild = child;
        
        //Split questionText so that only the question is set on the label and not the example
        String questionText = reviewData.getQuestionText().split("\n")[0];
        String header = Integer.toString(reviewData.getQuestionNumber()) + " : " + questionText;
        lblQuestionHeader.setText(header);
        
        vboxInfoContent.setSpacing(20);
        
        String notes = reviewData.getQuestionNotes().equals("") ? 
                        reviewText.get("RT1") : reviewData.getQuestionNotes();
        
        displayReviewData(reviewText.get("RT2"), reviewData.getQuestionAnswer(), false);
        displayReviewData(reviewText.get("RT3"), notes, false);
        displayReviewData(reviewText.get("RT4"), reviewData.getFollowUpResult(), false);
        displayReviewData(reviewText.get("RT5"), reviewData.getFollowUpText(), true);
    }
    
    /**
     * Used to display all relevant review data that has been saved throughout the diagnosis 
     * @param headerText Text for header of current data to be displayed
     * @param data Data to be displayed
     * @param followUp Whether or not it is for the follow up questions as requires different layout 
     */
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
            //If its for the follow up data split the data into its seperate part
            String[] followUpText = data.split("%");
            
            for(String text : followUpText)
            {
                //Loop through each part of the follow up answers and layout accordingly
                //depending on what type of follow up question it was i.e. checklist or just normal question
                if(text.split(";").length > 1)
                {
                    String[] checklistText = text.split(";");
                    Text checkHeader = new Text(reviewText.get("RT6")+"\n");
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
            //If its not part of the follow up check if a audio button is required
            if(headerText.equals(reviewText.get("RT3")) && data.equals("Audio"))
            {
                //If it is create the button and add an event handler to play the audio file
                JFXButton playButton = new JFXButton(reviewText.get("RT7"));
                playButton.getStylesheets().add("Styles/GreenBtnStyles.css");
                playButton.setTextFill(Color.WHITE);
                
                playButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event) -> {
                    playAudioFile();
                });
                
                tf.getChildren().add(playButton);
            }
            else
            {
                answer = new Text(data);
                answer.setFont(Font.font("Berlin Sans FB", FontWeight.NORMAL, 19));
                tf.getChildren().add(answer);
            }
        }
        tf.setTextAlignment(TextAlignment.JUSTIFY);
        
        vboxInfoContent.getChildren().add(tf);
    }
    
    private void playAudioFile()
    {
        //Use the child object passed from the individual review page to create the child folder and add that
        //to the saved audio file location on the SettingsManager
        String childFolder = String.format("%d-%s", currentChild.getChildId(), currentChild.getChildName());
        String audioPath = SettingsManager.getAudioFileLocation() + childFolder;
        File audioFile = new File(String.format("%s\\Question%d.wav", audioPath, reviewData.getQuestionNumber()));
        
        if(audioFile.exists())
        {
            Thread playThread = new Thread(() -> 
            {
                //Using the media and media player objects, find the file and play it back
                Media audioPlayback = new Media(audioFile.toURI().toString());
                MediaPlayer mediaPlayer = new MediaPlayer(audioPlayback);
                mediaPlayer.play();
            });
            playThread.start();
        }
        else
        {
            PopupText popup = LanguageManager.getPopupText(22);
            StageManager.loadPopupMessage(popup.getHeader(), popup.getMessage(), ButtonTypeEnum.OK);
        }
    }
}
