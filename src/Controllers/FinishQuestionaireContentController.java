package Controllers;

import Classes.DatabaseManager;
import Classes.QuestionaireManager;
import Classes.StageManager;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class FinishQuestionaireContentController implements Initializable 
{
    @FXML private Label lblScore;
    @FXML private Label lblRiskText;
    @FXML private Label lblHeader;
    @FXML private Label lblScoreText;
    @FXML private Button btnContinue;
    
    private boolean followUp;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {
        int atRiskQuestions = QuestionaireManager.getQuestionaireScore();
        String atRiskText;
        
        if(!QuestionaireManager.getFollowUpCompleted())
        {
            lblScore.setText(Integer.toString(atRiskQuestions) + " / 20");

            atRiskText = QuestionaireManager.getResultInfo(1);
            lblRiskText.setText(atRiskText);

            if(atRiskQuestions < 3 || atRiskQuestions > 7)
            {
                btnContinue.setText("Done");
                followUp = false;
            }
            else
                followUp = true;
        }
        else
        {
            lblHeader.setText("M-CHAT-R/F Diagnosis");
            lblScoreText.setText("You have now completed the second part of the Diagnosis.\n" +
                                "The score below represents the amount of follow up questions answered that had a Fail result.");
            btnContinue.setText("Done");
            
            int failedQuestions = QuestionaireManager.getFailedQuestions();
            lblScore.setText(Integer.toString(failedQuestions) + " / " + Integer.toString(atRiskQuestions));
            
            atRiskText = QuestionaireManager.getResultInfo(2);
            lblRiskText.setText(atRiskText);         
            
            followUp = false;  
        }
    }    
    
    public void btnContinue_Action(ActionEvent event)
    {
        if(!QuestionaireManager.getFollowUpCompleted())
        {
            if(followUp)
            {
                QuestionaireManager.saveFirstStageScore();
                StageManager.loadContentScene(StageManager.FOLLOWUP);
            }
            else
            {
                QuestionaireManager.saveFirstStageScore();
                QuestionaireManager.resetQuestionaireManager();
                StageManager.loadContentScene(StageManager.INSTRUCTIONS);
                StageManager.setInProgress(false);
            }
        }
        else
        {
            QuestionaireManager.saveSecondStageScore();
            QuestionaireManager.resetQuestionaireManager();
            StageManager.loadContentScene(StageManager.INSTRUCTIONS);
            StageManager.setInProgress(false);
        }
    }
}
