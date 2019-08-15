package Controllers;

import Classes.Child;
import Classes.QuestionSet;
import Managers.DatabaseManager;
import Classes.ReviewData;
import Managers.StageManager;
import ControlControllers.QuestionReviewControlController;
import Managers.LogManager;
import Managers.QuestionaireManager;
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
    @FXML private VBox vboxMainSet;
    @FXML private Label lblReviewHeader;
    @FXML private Label lblStage1Score;
    @FXML private Label lblStage1Result;
    @FXML private Label lblStage2Score;
    @FXML private Label lblStage2Result;
    
    @FXML private VBox vboxOtherSet;
    @FXML private Label lblOtherScore;
    @FXML private Label lblOtherRisk;
    
    @FXML private VBox vboxReviewControls;
    
    private final LogManager logManager = new LogManager();
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {}    
    
    public void setupIndividualReviewContent(Child child)
    {
        DatabaseManager dbManager = new DatabaseManager();
        ArrayList<ReviewData> reviewData;
        
        //Get and set the labels at the top of the form to be the diagnosis result values from
        //the child object
        String header = lblReviewHeader.getText().replace("#child#", child.getChildName());
        lblReviewHeader.setText(header);
        
        //Get the questionSet that was used for this childs screening
        QuestionSet questionSet = QuestionaireManager.getSpecificQuestionSet(child.getDiagnosisResult().getQuestionSetName());
        
        String s1Score = String.format("%d / %d", child.getDiagnosisResult().getStageOneScore(), 
                                                    questionSet.getNumberOfQuestions());
        String s1Risk = child.getDiagnosisResult().getStageOneRisk();
        
        //If child under review was using the default M-CHAT-R/F set labels as usual
        if(questionSet.getSetName().equals("M-CHAT-R/F"))
        {
            String s2Score = String.format("%d / %d", child.getDiagnosisResult().getStageTwoScore(), 
                                                            child.getDiagnosisResult().getStageOneScore());
            String screening = child.getDiagnosisResult().getOverallScreening();
            
            lblStage1Score.setText(s1Score);
            lblStage1Result.setText(s1Risk);
            lblStage2Score.setText(s2Score);
            lblStage2Result.setText(screening);
        }
        else
        {
            //Else if a different Question Set was used, hide default vbox and use other vbox and labels
            vboxMainSet.setVisible(false);
            vboxOtherSet.setVisible(true);
            
            String scoreText = lblOtherScore.getText().replace("#setName#", questionSet.getSetName());
            scoreText = scoreText.replace("#score#", s1Score);
            lblOtherScore.setText(scoreText);
            
            String riskText = lblOtherRisk.getText().replace("#setName#", questionSet.getSetName());
            riskText = riskText.replace("#risk#", s1Risk);
            lblOtherRisk.setText(riskText);
        }
        
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
            logManager.ErrorLog("Error when loading QuestionReviewControlController - " + ex.getMessage());
        }
    }
    
    public void btnBack_Action(ActionEvent event)
    {
        StageManager.loadContentScene(StageManager.REVIEW);
    }
}
