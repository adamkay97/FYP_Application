package ControlControllers;

import Classes.Child;
import Classes.QuestionSet;
import Classes.ReviewData;
import Managers.StageManager;
import Controllers.QuestionReviewContentController;
import Enums.QuestionAnswer;
import Managers.LanguageManager;
import Managers.QuestionaireManager;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
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
    
    private Child currentChild;
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
            questionContent.setupQuestionReviewContent(reviewData, currentChild);
            
            StageManager.loadContentSceneParent(root);
            LanguageManager.setFormText("QuestionReview", StageManager.getRootScene());
        } 
        catch (IOException ex) 
        {
            System.out.println("Error when loading QuestionReviewContent - " + ex.getMessage());
        }
    }
    
    public void setUpReviewControl(ReviewData data, Child child)
    {
        HashMap<String, String> controlText = LanguageManager.getSpecifiedText("QuestionReviewControl");
        
        QuestionSet questionSet = QuestionaireManager.getSpecificQuestionSet(child.getDiagnosisResult().getQuestionSetName());
        int qId = data.getQuestionNumber();
        String answer = data.getQuestionAnswer();
        
        lblQuestionNumber.setText(Integer.toString(qId));
        lblQuestionAnswer.setText(controlText.get("QRC1") + answer);
        
        String risk;
        QuestionAnswer atRiskResponse = questionSet.getQuestion(qId).getAtRiskResponse();
        
        if(atRiskResponse == QuestionAnswer.YES && answer.equals("Yes"))
            risk = "Positive";
        else if(atRiskResponse == QuestionAnswer.NO && answer.equals("No")) 
            risk = "Positive";
        else
            risk = "Negative";
        
        lblASDRisk.setText(controlText.get("QRC2") +risk);
        btnView.setText(controlText.get("QRC3"));
        
        reviewData = data;
        currentChild = child;
    }
}
