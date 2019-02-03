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
                                    "The score below represents the amount of follow up questions answered that were a Fail.");
            btnContinue.setText("Done");
            
            int failedQuestions = QuestionaireManager.getFailedQuestions();
            lblScore.setText(Integer.toString(failedQuestions) + " / " + Integer.toString(atRiskQuestions));
            
            atRiskText = QuestionaireManager.getResultInfo(2);
            lblRiskText.setText(atRiskText);
            
//            if(failedQuestions >= 2)
//            {
//                lblRiskText.setText("Screened Positive: 2 or more of the follow up questions were failed wich deems the "
//                                    + "overall diagnosis as a positive screening. It is strongly recommended that your child is referred "
//                                    + "for early intervention and diagnostic testing as soon as possible.");
//            }
//            else
//            {
//                lblRiskText.setText("Screened Negative: 1 or less of the follow up questions were failed wich deems the "
//                                    + "overall diagnosis as a negative screening. If you or the healthcare provider still has"
//                                    + "concerns about ASD then children should be referred for evaluation no matter the score from this diagnosis.");
//            }
            followUp = false;  
        }
    }    
    
    public void btnContinue_Action(ActionEvent event)
    {
        if(!QuestionaireManager.getFollowUpCompleted())
        {
            if(followUp)
            {
                QuestionaireManager.saveFirstStageScore(false);
                StageManager.loadContentScene(StageManager.FOLLOWUP);
            }
            else
            {
                QuestionaireManager.saveFirstStageScore(true);
                StageManager.loadContentScene(StageManager.INSTRUCTIONS);
            }
        }
        else
        {
            //Save final score
            StageManager.loadContentScene(StageManager.INSTRUCTIONS);
        }
    }
}
