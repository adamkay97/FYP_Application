package ControlControllers;

import Controllers.QuestionaireContentController;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;

public class QuestionTextAnswerControlController implements Initializable 
{
    @FXML private TextArea txtNotes;
    
    private QuestionaireContentController questionaireContentController;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {}

    @FXML public void btnYes_Action(ActionEvent event) 
    {
        if(questionaireContentController.handleYesTextAction(txtNotes.getText()))
            txtNotes.setText("");
    }

    @FXML public void btnNo_Action(ActionEvent event) 
    {
        if(questionaireContentController.handleNoTextAction(txtNotes.getText()))
            txtNotes.setText("");
    }
    
    public void setupQuestionaireController(QuestionaireContentController controller)
    {
        questionaireContentController = controller;
    }
}
