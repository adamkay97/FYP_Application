package ControlControllers;

import Classes.PopupText;
import Managers.StageManager;
import Controllers.FollowUpContentController;
import Enums.ButtonTypeEnum;
import Enums.FlowBranchEnum;
import Managers.LanguageManager;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

public class ChecklistControlController implements Initializable 
{
    @FXML private AnchorPane rootPane;
    @FXML private Label lblChecklistText;
    @FXML private VBox vboxCheckExamples;
    
    private FollowUpContentController followUpController;
    private boolean usesPassFail, usesOther;
    private int passCount, failCount, otherCount, totalCount;
    private String otherText;
    
    private ArrayList<String> passFailList;
    private ArrayList<ToggleGroup> radBtnList;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {
        passCount = 0; failCount = 0; otherCount = 0; totalCount = 0;
        usesPassFail = false; usesOther = false;
        otherText = "";
        
        passFailList = new ArrayList<>();
        radBtnList = new ArrayList<>();
    }    
    
    @FXML void btnContinue_Action(ActionEvent event) 
    {        
        //Checks to see if the count equals the total count of all
        //the controls to make sure no answer has been asked.
        int clickedCount = passCount + failCount + otherCount;
        
        //If there is an "Other" control remove one from the total count 
        //as it isnt required to be answered before moving on.        
        if(clickedCount == (usesOther ? totalCount-1 : totalCount))
        {
            FlowBranchEnum result = parseAnswers();
            followUpController.loadNextFollowUpPart(result);
        }
        else
        {
            PopupText popup = LanguageManager.getPopupText(19);
            StageManager.loadPopupMessage(popup.getHeader(), popup.getMessage(), ButtonTypeEnum.OK);
        }
    }
    
    public void saveYesNoAnswer(String answer, String question, String passFail, boolean selected)
    {
        //Check to see whether or not the checklist is comprised of both pass and fail options
        if(usesPassFail)
        {
            //If it is increment the relevant counter for pass, fail or other (Answers that dont count towards an outcome)
            //Add question and answer to passFailList for use if both Pass and Fail examples are selected
            if(answer.equals("YES") && passFail.equals("PASS"))
            {
                passCount++;
                passFailList.add(question + ";" + "PASS");
            }
            else if(answer.equals("YES") && passFail.equals("FAIL"))
            {
                failCount++;
                passFailList.add(question + ";" + "FAIL");
            }
            else
                otherCount++;
            
            if(selected)
            {
                //If the toggle group has already had a button selected, if the answer is then changed
                //to the opposite button decrement the opposite counter.
                if(answer.equals("NO") && passFail.equals("PASS"))
                {
                    passCount--;
                    passFailList.remove(question + ";" + "PASS");
                }
                else if(answer.equals("NO") && passFail.equals("FAIL"))
                {
                    failCount--;
                    passFailList.remove(question + ";" + "FAIL");
                }
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
    
    private FlowBranchEnum parseAnswers()
    {
        int qNumber = followUpController.getQuestionNumber();
        FlowBranchEnum result = FlowBranchEnum.Root;

        //Ugly method but works by calculating the path that the flow should carry 
        //on in depending on the type of question and whether its for q 14 or 15
        //which require special cases.
        if(usesPassFail)
        {
           if(passCount >= 1 && failCount == 0)
               result = FlowBranchEnum.PassOnly;
           else if(passCount == 0 && failCount >= 1)
               result = FlowBranchEnum.FailOnly;
           else if(passCount >= 1 && failCount >= 1)
               result = FlowBranchEnum.Both;
           else
               result = FlowBranchEnum.FailOnly;

           //Special case required for Question 5 as there is no possible "Both"
           //branch to traverse after in the tree
           if(qNumber == 5 && result == FlowBranchEnum.Both)
               result = FlowBranchEnum.FailOnly;

           followUpController.setChecklistAnswers(passFailList);
        }
        else
        {
            switch (qNumber) 
            {
               case 14:
                   if(failCount == totalCount)
                       result = FlowBranchEnum.NoAll;
                   else if(passCount >= 2)
                       result = FlowBranchEnum.Yes2OM;
                   else if(passCount == 1)
                       result = FlowBranchEnum.Yes1O;
                   break;

               case 15:
                   if(passCount >= 2)
                       result = FlowBranchEnum.Yes2OM;
                   else if(passCount <= 1)
                       result = FlowBranchEnum.No1ON;
                   break;

               default:
                   if(failCount == (usesOther ? totalCount-1 : totalCount))
                       result = FlowBranchEnum.NoAll;
                   if(passCount >= 1)
                       result = FlowBranchEnum.YesAny;
                   break;
            }
        }
        
        //Once all questions have been answered loop through each toggle group
        //to get the answers and store them in a string on the current node for review purposes
        String checkListAnswers = "";
        for(ToggleGroup toggleGroup : radBtnList)
            checkListAnswers += toggleGroup.getSelectedToggle().getUserData().toString() + ";";
        
        //If there is an other option and its not been left blank append to answer string
        //else remove last ';' from string
        if(usesOther && !otherText.equals(""))
            checkListAnswers += otherText;
        else
            checkListAnswers = checkListAnswers.substring(0, checkListAnswers.length()-1);
            
        followUpController.setCurrentNodeAnswer(checkListAnswers);
        return result;
    }
    
    private void loadExamples(String[] examples, String isPassFail)
    {
        try
        {
            FXMLLoader loader;
            Parent root;
            
            //For each example create a new control
            for(String example : examples)
            {
                //Check to see if example is for providing an "other" text input 
                if(!example.contains("(describe)"))
                {
                    ToggleGroup yesNoGroup = new ToggleGroup();
                    
                    //Load control and pass it the text and what type it is
                    loader = new FXMLLoader(getClass().getResource("/Controls/YesNoExampleControl.fxml"));
                    root = (Parent)loader.load();

                    YesNoExampleControlController yesNoControl = loader.<YesNoExampleControlController>getController();
                    yesNoControl.setExampleControl(example, isPassFail, yesNoGroup);
                    yesNoControl.setCheckListController(this);
                    
                    //Add toggleGroup to list for review purposes
                    radBtnList.add(yesNoGroup);
                    
                    LanguageManager.setFormText("StageTwoYesNoExample", StageManager.getRootScene());
                }
                else
                {
                    //If it is load Other control and pass it the text
                    loader = new FXMLLoader(getClass().getResource("/Controls/OtherExampleControl.fxml"));
                    root = (Parent)loader.load();

                    OtherExampleControlController otherControl = loader.<OtherExampleControlController>getController();
                    otherControl.setExampleControl(example);
                    otherControl.setCheckListController(this);
                    usesOther = true;
                    
                    LanguageManager.setFormText("StageTwoOtherExample", StageManager.getRootScene());
                }
                vboxCheckExamples.getChildren().add(root);
            }
        }
        catch (IOException ex)
        {
            System.out.println("Failed when loaded Checklist examples - " + ex.getMessage());
        }
    }
    
    public void setOtherText(String text) { otherText = "Other (describe) = " + text; }
    public void setFollowUpController(FollowUpContentController controller) { followUpController = controller; }
}
