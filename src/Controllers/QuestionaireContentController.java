package Controllers;

import Classes.QuestionAnswer;
import Classes.QuestionaireManager;
import Classes.StageManager;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

public class QuestionaireContentController implements Initializable 
{
    @FXML private Button btnYes;
    @FXML private Button btnNo;
    @FXML private Label lblQuestionText;
    @FXML private Label lblQuestionHeader;
    @FXML private TextArea txtNotes;
    
    private int qIndex;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {
        qIndex = 1;
        setQuestionText(qIndex);
    }
    
    @FXML void btnYes_Action(ActionEvent event) 
    {
        String notes = txtNotes.getText();
        QuestionaireManager.saveQuestionAnswer(qIndex, QuestionAnswer.YES, notes);
        processAnswer();
    }

    @FXML void btnNo_Action(ActionEvent event) 
    {
        String notes = txtNotes.getText();
        QuestionaireManager.saveQuestionAnswer(qIndex, QuestionAnswer.NO, notes);
        processAnswer();     
    }
    
    private void processAnswer()
    {
         if(qIndex != 20)
            setQuestionText(++qIndex);
        else
            StageManager.loadContentScene(StageManager.FINISH);
    }
    
    private void setQuestionText(int index)
    {
        String text = QuestionaireManager.getQuestionText(index);
        lblQuestionText.setText(text);

        lblQuestionHeader.setText("Question " + index + ":");
    }
}
