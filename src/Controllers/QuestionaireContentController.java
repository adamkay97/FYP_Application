/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
import javafx.scene.layout.*;


/**
 * FXML Controller class
 *
 * @author Adam
 */
public class QuestionaireContentController implements Initializable 
{
    @FXML private Button btnYes;
    @FXML private Button btnNo;
    @FXML private Label lblQuestionText;
    @FXML private Label lblQuestionHeader;
    
    private int qIndex;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {
        qIndex = 1;
        setQuestionText(qIndex);
    }
    
    @FXML void btnYes_Action(ActionEvent event) 
    {
        QuestionaireManager.saveQuestionAnswer(qIndex, QuestionAnswer.YES);
        processAnswer();
    }

    @FXML void btnNo_Action(ActionEvent event) 
    {
        QuestionaireManager.saveQuestionAnswer(qIndex, QuestionAnswer.NO);
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
