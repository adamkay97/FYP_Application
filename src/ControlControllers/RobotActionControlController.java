package ControlControllers;

import Controllers.QuestionaireContentController;
import Managers.LanguageManager;
import Managers.StageManager;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

public class RobotActionControlController implements Initializable 
{
    private QuestionaireContentController questionaireContentController;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {
        LanguageManager.setFormText("RobotControl", StageManager.getRootScene());
    }    
    
    @FXML public void btnPlay_Action(ActionEvent event) 
    {
        //Call play function once current FXML threads have finished,
        //by using Platform.runLater
        questionaireContentController.handlePlayAction();
    }
    
    public void setupQuestionaireController(QuestionaireContentController controller)
    {
        questionaireContentController = controller;
    }
    
}
