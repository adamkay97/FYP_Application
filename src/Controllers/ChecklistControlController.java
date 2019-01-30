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
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class ChecklistControlController implements Initializable 
{
    @FXML private Label lblChecklistText;
    @FXML private Button btnContinue;
    @FXML private VBox vboxCheckExamples;
    @FXML private ScrollPane scrlPaneInstr;
    
    private FollowUpContentController followUpController;
    private boolean usesPassFail;
    private int passCount, failCount, otherCount, totalCount;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {
        passCount = 0; failCount = 0; otherCount = 0; totalCount = 0;
        usesPassFail = false;
    }    
    
    @FXML void btnContinue_Action(ActionEvent event) 
    {
        int clickedCount = passCount + failCount + otherCount;
        if(clickedCount == totalCount)
        {
            parseAnswers();
           
            
        }
        else
        {
            String errMsg = "Please make sure you have answered all of the Yes/No questions before proceeding.";
            StageManager.loadPopupMessage("Warning", errMsg, ButtonTypeEnum.OK);
        }
    }
    
    public void saveYesNoAnswer(String answer, String passFail, boolean selected)
    {
        //Check to see whether or not the checklist is comprised of both pass and fail options
        if(usesPassFail)
        {
            //If it is increment the relevant counter for pass, fail or other (Answers that dont count towards an outcome)
            if(answer.equals("YES") && passFail.equals("PASS"))
                passCount++;
            else if(answer.equals("YES") && passFail.equals("FAIL"))
                failCount++;
            else
                otherCount++;
            
            if(selected)
            {
                //If the toggle group has already had a button selected, if the answer is then changed
                //to the opposite button decrement the opposite counter.
                if(answer.equals("NO") && passFail.equals("PASS"))
                    passCount--;
                else if(answer.equals("NO") && passFail.equals("FAIL"))
                    failCount--;
                else
                    otherCount--;
            }
        }
        else
        {
            //For just Yes/No checklists, increment the relevant counter
            if(answer.equals("YES"))
                passCount++;
            else if(answer.equals("NO"))
                failCount++;
            
            if(selected)
            {
                //Decrement opposite counter to newly selected button
                if(answer.equals("YES"))
                    failCount--;
                else if(answer.equals("NO"))
                    passCount--;
            }
        }
    }
    
    public void setupChecklist(String text) 
    {
        //Split the text from the db on '%' to get the title '[0]'
        String[] splitText = text.split("%");
        lblChecklistText.setText(splitText[0]);

        //If there are only Yes/No examples length will be 2
        if(splitText.length == 2)
        {
            //Split on ';' and loadExamples to the VBox. 
            String[] examples = splitText[1].split(";");
            totalCount = examples.length;
            loadExamples(examples, "");
        }
        else
        {
            //Else if there are Pass and Fail examples load both into the VBox
            //Setting either "PASS" or "FAIL" on the control
            String[] passExamples = splitText[1].split(";");
            String[] failExamples = splitText[2].split(";");
            totalCount = passExamples.length + failExamples.length;
            
            loadExamples(passExamples, "PASS");
            loadExamples(failExamples, "FAIL");
            
            usesPassFail = true;
        }

    }
    
    private void parseAnswers()
    {
         ArrayList<FlowBranchEnum> followingBranches = followUpController.getFollowingBranches();
    }
    
    private void loadExamples(String[] examples, String isPassFail)
    {
        try
        {
            //For each example create a new control
            for(String example : examples)
            {
                //Load control and pass it the text and what type it is
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Controls/YesNoExampleControl.fxml"));
                Parent root = (Parent)loader.load();

                YesNoExampleControlController yesNoControl = loader.<YesNoExampleControlController>getController();
                yesNoControl.setExampleControl(example, isPassFail);
                yesNoControl.setCheckListController(this);

                vboxCheckExamples.getChildren().add(root);
            }
        }
        catch (IOException ex)
        {
            System.out.println("Failed when loaded Checklist examples - " + ex.getMessage());
        }
    }
    
    public void setFollowUpController(FollowUpContentController controller) { followUpController = controller; }
}
