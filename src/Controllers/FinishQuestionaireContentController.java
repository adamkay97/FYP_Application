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
    @FXML private Button btnContinue;
    
    private boolean followUp;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {
        int atRiskQuestions = QuestionaireManager.getQuestionaireScore();
        lblScore.setText(Integer.toString(atRiskQuestions) + " / 20");
        
        String atRiskText = QuestionaireManager.getResultInfo();
        lblRiskText.setText(atRiskText);
        
        if(atRiskQuestions < 3 || atRiskQuestions > 7)
        {
            btnContinue.setText("Done");
            followUp = false;
        }
        else
            followUp = true;
    }    
    
    public void btnContinue_Action(ActionEvent event)
    {
        if(followUp)
        {
            //SaveResult()
        }
        else
        {
            QuestionaireManager.saveFinalScore();
            StageManager.loadContentScene(StageManager.INSTRUCTIONS);
        }
    }
}
