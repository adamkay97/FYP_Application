package Controllers;

import Classes.StageManager;
import Enums.ButtonTypeEnum;
import Enums.FlowBranchEnum;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;

public class PassFailControlController implements Initializable 
{
    @FXML private Label lblChecklistText;
    @FXML private VBox vboxCheckExamples;
    
    private FollowUpContentController followUpController;
    private ToggleGroup answerGroup;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {
        answerGroup = new ToggleGroup();
    }    
    
    @FXML void btnContinue_Action(ActionEvent event) 
    {
        FlowBranchEnum result;
        
        if(answerGroup.getSelectedToggle() != null)
        {
            String[] splitAnswer = answerGroup.getSelectedToggle().getUserData().toString().split(";");
            
            if(splitAnswer[1].equals("PASS"))
                result = FlowBranchEnum.Pass;
            else
                result = FlowBranchEnum.Fail;
            
            followUpController.setCurrentNodeAnswer(lblChecklistText.getText() + " = " + splitAnswer[0]);
            followUpController.loadNextFollowUpPart(result);
        }
        else
        {
            String errMsg = "Please make sure you have selected one of the options before proceeding.";
            StageManager.loadPopupMessage("Warning", errMsg, ButtonTypeEnum.OK);
        }
    }
    
    private void loadAnswers(ArrayList<String> answers)
    {
        try
        {
            FXMLLoader loader;
            Parent root;
            
            //For each example create a new control
            for(String answer : answers)
            {
                String[] splitAnswer = answer.split(";");
                
                //If it is load Other control and pass it the text
                loader = new FXMLLoader(getClass().getResource("/Controls/PassFailExampleControl.fxml"));
                root = (Parent)loader.load();

                PassFailExampleControlController passFailControl = loader.<PassFailExampleControlController>getController();
                passFailControl.setExampleControl(splitAnswer[0], splitAnswer[1], answerGroup);
                //otherControl.setCheckListController(this);
                
                vboxCheckExamples.getChildren().add(root);
            }
        }
        catch (IOException ex)
        {
            System.out.println("Failed when loaded Checklist examples - " + ex.getMessage());
        }
    }
    
    public void setQuestionText(String questionText, ArrayList<String> answers)
    {
        lblChecklistText.setText(questionText);
        loadAnswers(answers);
    }
    
    public void setFollowUpController(FollowUpContentController controller) { followUpController = controller; }
}
