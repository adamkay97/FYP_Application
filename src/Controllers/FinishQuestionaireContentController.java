package Controllers;

import Enums.ButtonTypeEnum;
import Managers.DatabaseManager;
import Managers.LanguageManager;
import Managers.QuestionaireManager;
import Managers.RobotManager;
import Managers.SettingsManager;
import Managers.StageManager;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
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
        //Setup form depending on which stage ending it is being used for
        setupFinishedStage();
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
    
    private void setupFinishedStage()
    {
        int atRiskQuestions = QuestionaireManager.getQuestionaireScore();
        String atRiskText;
        
        //Set default IDs for elements with changeable text 
        lblHeader.setId("F11");
        lblScoreText.setId("F31");
        btnContinue.setId("F51");
        
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
                btnContinue.setId("F52");
                followUp = false;
            }
            else
                followUp = true;
        }
        else
        {            
            lblHeader.setId("F12");
            lblScoreText.setId("F32");
            btnContinue.setId("F52");
            
            int failedQuestions = QuestionaireManager.getFailedQuestions();
            lblScore.setText(Integer.toString(failedQuestions) + " / " + Integer.toString(atRiskQuestions));
            
            atRiskText = QuestionaireManager.getResultInfo(2);
            lblRiskText.setText(atRiskText);         
            
            followUp = false;  
        }
    }
}
