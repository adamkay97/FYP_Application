package Controllers;

import Classes.Child;
import Managers.DatabaseManager;
import Classes.ReviewData;
import Managers.StageManager;
import ControlControllers.QuestionReviewControlController;
import Managers.LanguageManager;
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

public class IndividualReviewContentController implements Initializable 
{
    @FXML private Label lblReviewHeader;
    @FXML private Label lblStage1Score;
    @FXML private Label lblStage1Result;
    @FXML private Label lblStage2Score;
    @FXML private Label lblStage2Result;
    @FXML private VBox vboxReviewControls;
    
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        //LanguageManager.setFormText("IndividualReview", StageManager.getRootScene());
    }    
    
    public void setupIndividualReviewContent(Child child)
    {
        DatabaseManager dbManager = new DatabaseManager();
        ArrayList<ReviewData> reviewData;
        
        //Get and set the labels at the top of the form to be the diagnosis result values from
        //the child object
        String header = lblReviewHeader.getText().replace("#child#", child.getChildName());
        String s1Score = String.format(lblStage1Score.getText() + "%d / 20", child.getDiagnosisResult().getStageOneScore());
        String s1Risk = lblStage1Result.getText() + child.getDiagnosisResult().getStageOneRisk();
        String s2Score = String.format(lblStage2Score.getText() + "%d / %d", child.getDiagnosisResult().getStageTwoScore(), 
                                                                                child.getDiagnosisResult().getStageOneScore());
        String screening = lblStage2Result.getText() + child.getDiagnosisResult().getOverallScreening();
        
        lblReviewHeader.setText(header);
        lblStage1Score.setText(s1Score);
        lblStage1Result.setText(s1Risk);
        lblStage2Score.setText(s2Score);
        lblStage2Result.setText(screening);
        
        if(dbManager.connect())
        {
            reviewData = dbManager.loadReviewData(child.getChildId(), child.getCurrentUserId());
            dbManager.disconnect();
            
            loadQuestionReviewControls(reviewData, child);
        }
    }
    
    private void loadQuestionReviewControls(ArrayList<ReviewData> reviewData, Child child)
    {
        FXMLLoader loader;
        Parent root;
        try
        {
            for(ReviewData data : reviewData)
            {
                //Load control and pass it the text
                loader = new FXMLLoader(getClass().getResource("/Controls/QuestionReviewControl.fxml"));
                root = (Parent)loader.load();

                QuestionReviewControlController reviewControl = loader.<QuestionReviewControlController>getController();
                reviewControl.setUpReviewControl(data, child);
                
                vboxReviewControls.getChildren().add(root);
            }
        }
        catch(IOException ex)
        {
            System.out.println("Error when loading QuestionReviewControlController - " + ex.getMessage());
        }
    }
    
    public void btnBack_Action(ActionEvent event)
    {
        StageManager.loadContentScene(StageManager.REVIEW);
    }
}
