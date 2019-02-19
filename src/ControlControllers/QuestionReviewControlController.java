package ControlControllers;

import Classes.ReviewData;
import Classes.StageManager;
import Controllers.QuestionReviewContentController;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class QuestionReviewControlController implements Initializable 
{
    @FXML private Label lblQuestionAnswer;
    @FXML private Label lblASDRisk;
    @FXML private Button btnView;
    @FXML private Label lblQuestionNumber;
    
    private ReviewData reviewData;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {}    
    
    @FXML public void btnView_Action(ActionEvent event) 
    {
        try 
        {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Forms/QuestionReviewContent.fxml"));
            Parent root = (Parent)loader.load();
            
            QuestionReviewContentController questionContent = loader.<QuestionReviewContentController>getController();
            questionContent.setupQuestionReviewContent(reviewData);
            
            StageManager.loadContentSceneParent(root);
        } 
        catch (IOException ex) 
        {
            System.out.println("Error when loading QuestionReviewContent - " + ex.getMessage());
        }
    }
    
    public void setUpReviewControl(ReviewData data)
    {
        int qId = data.getQuestionNumber();
        String answer = data.getQuestionAnswer();
        
        lblQuestionNumber.setText(Integer.toString(qId));
        lblQuestionAnswer.setText("Question Answer: " + answer);
        
        String risk;
        
        if((qId == 2 || qId == 5 || qId == 12) && answer.equals("Yes"))
            risk = "Positive";
        else if((qId != 2 && qId != 5 && qId != 12) && answer.equals("No")) 
            risk = "Positive";
        else
            risk = "Negative";
        
        lblASDRisk.setText("ASD Screening: " + risk);
        
        reviewData = data;
    }
    
}
