package Controllers;

import Classes.QuestionAnswer;
import Classes.QuestionaireManager;
import Classes.StageManager;
import Enums.ButtonTypeEnum;
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
        
        if(checkValidNotes(notes, true))
        {
            QuestionaireManager.saveQuestionAnswer(qIndex, QuestionAnswer.YES, notes);
            processAnswer();
        }
    }

    @FXML void btnNo_Action(ActionEvent event) 
    {
        String notes = txtNotes.getText();
        
        if(checkValidNotes(notes, false))
        {
            QuestionaireManager.saveQuestionAnswer(qIndex, QuestionAnswer.NO, notes);
            processAnswer();
        }
    }
    
    private void processAnswer()
    {
        if(qIndex != 20)
        {
            setQuestionText(++qIndex);
            txtNotes.setText("");
        }
        else
            StageManager.loadContentScene(StageManager.FINISH);
    }
    
    /**
     * Checks to make sure the notes have some text written if the answer given 
     * indicates a risk of ASD.
     * @param notes Notes text from textArea
     * @param isYes Boolean to say whether the input has come from the Yes or No button
     * @return 
     */
    private boolean checkValidNotes(String notes, boolean isYes)
    {
        boolean valid;
        
        if(notes.equals(""))
        {
            //Checks whether the questions answer is a negative response by
            //checking against questions 2,5,12 which have alternate negative responses
            if(isYes && (qIndex == 2 || qIndex == 5 || qIndex == 12))
                valid = false;
            else if (!isYes && (qIndex != 2 && qIndex != 5 && qIndex != 12))
                valid = false;
            else
                valid = true;
        }
        else
            valid = true;
        
        //If notes arent valid display popup message to screen
        if(!valid)
        {
            String msg = "Please add notes for why you have selected this answer. "
                   + "If you have nothing further to say please input 'N/A'.";
            StageManager.loadPopupMessage("Warning", msg, ButtonTypeEnum.OK);
        }
        
        return valid;
    }
    
    /**
     * Sets the label text to the question stored in the QuestionaireManager
     * @param index Index of question (or question number)
     */
    private void setQuestionText(int index)
    {
        String text = QuestionaireManager.getQuestionText(index);
        lblQuestionText.setText(text);

        lblQuestionHeader.setText("Question " + index + ":");
    }
}
