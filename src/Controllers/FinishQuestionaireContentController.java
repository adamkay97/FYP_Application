package Controllers;

import Classes.QuestionaireManager;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

public class FinishQuestionaireContentController implements Initializable 
{
    @FXML private Label lblScore;
    @FXML private Label lblRiskText;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {
        int atRiskQuestions = QuestionaireManager.getQuestionaireScore();
        lblScore.setText(Integer.toString(atRiskQuestions) + " / 20");
        
        String atRiskText = QuestionaireManager.getResultInfo();
        lblRiskText.setText(atRiskText);
    }    
    
    public void btnContinue_Action(ActionEvent event)
    {
        
    }
    
}
