package Controllers;

import Enums.ButtonTypeEnum;
import Managers.DatabaseManager;
import Managers.QuestionaireManager;
import Managers.RobotManager;
import Managers.SettingsManager;
import Managers.StageManager;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

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
            if(SettingsManager.getUsesNaoRobot())
            {
                //Run end behaviour in seperate thread so the rest of the application isn't held up
                Thread robotThread = new Thread(() -> {
                    RobotManager.runStartEnd(false);
                });
                robotThread.start();
            }
            
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
       Thread finishQuestionaire;
       boolean save = false;
        
        if(!QuestionaireManager.getFollowUpCompleted())
        {
            if(followUp)
            {
                QuestionaireManager.saveFirstStageScore(followUp);
                StageManager.loadContentScene(StageManager.FOLLOWUP);
            }
            else
            {   
                //Run saving of questionaire score in seperate thread so doesnt hold up the
                //rest of the application
                finishQuestionaire = new Thread(() -> {
                    QuestionaireManager.saveFirstStageScore(followUp);
                    QuestionaireManager.resetQuestionaireManager();
                });
                finishQuestionaire.start();
                
                StageManager.loadContentScene(StageManager.INSTRUCTIONS);
                StageManager.setInProgress(false);
            }
        }
        else
        {
            //Run saving of second stage results in a seperate thread 
            //to stop the form loading from being held up
            finishQuestionaire = new Thread(() -> {
                QuestionaireManager.saveSecondStageScore();
                QuestionaireManager.resetQuestionaireManager();
            });
            finishQuestionaire.start();
            
            StageManager.loadContentScene(StageManager.INSTRUCTIONS);
            StageManager.setInProgress(false);
        }
    }
    
//    public boolean saveResult()
//    {
//        String msg = "Do you wish to save the result of this Diagnosis for review purposes?" ;
//        boolean saveResult = StageManager.loadPopupMessage("Information", msg, ButtonTypeEnum.YESNO);
//
//        if(saveResult)
//        {
//            StageManager.loadForm(StageManager.LOGIN, new Stage());
//            
//            if(StageManager.getCurrentUser() != null)
//                return true;
//            else
//                return false;
//        }
//        else
//            return false;
//    }
}
