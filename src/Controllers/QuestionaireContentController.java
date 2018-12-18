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
     
    private void setQuestionText(int index)
    {
        String text = "If you point at something across the room, does your child look at it?\n\n" +
                        "(For Example, if you point at a toy or an animal, does your child look at the toy or animal?)";
        lblQuestionText.setText(text);

        lblQuestionHeader.setText("Question " + index + ":");
    }
}
