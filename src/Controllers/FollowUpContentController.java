package Controllers;

import Managers.StageManager;
import Managers.QuestionaireManager;
import ControlControllers.ChecklistControlController;
import ControlControllers.PassFailControlController;
import ControlControllers.ExampleControlController;
import ControlControllers.YesNoControlController;
import Classes.*;
import Enums.FlowBranchEnum;
import Enums.QuestionTypeEnum;
import Managers.LanguageManager;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class FollowUpContentController implements Initializable 
{
    @FXML private StackPane stackPaneFollowUp;
    @FXML private Label lblQuestionHeader;
    
    private ArrayList<FollowUpFlow> followUpList;
    private FollowUpFlow currentFollowUp;
    private ArrayList<String> checkListAnswers;
    private int fIndex;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {
        ArrayList<Integer> flaggedQuestions = QuestionaireManager.getFlaggedQuestions();
        followUpList = new ArrayList<>();
        currentFollowUp = new FollowUpFlow();
        checkListAnswers = new ArrayList<>();
        fIndex = 0;
        
        //Initialize followUpList with the FollowUpFlow of each flagged questions
        for (int i : flaggedQuestions)
            followUpList.add(QuestionaireManager.getFollowUpFlow(i));
        
        setupFollowUpQuestion();
    }
    
    private void setupFollowUpQuestion()
    {
        //Check there are still follow up questions to be asked
        if(fIndex < followUpList.size())
        {
            //If there are, set the currentFollowUp to be the right questions
            currentFollowUp = followUpList.get(fIndex);
            int qNumber = currentFollowUp.getQuestionNumber();
            String header = qNumber + " : " +
                                currentFollowUp.getCurrentNodeText().replace("#child#", 
                                    QuestionaireManager.getCurrentChild().getChildName());

            setQuestionHeader(header);

            loadNextFollowUpPart(FlowBranchEnum.Start);
        }
        else
        {
            int totalFail = 0;
            
            //Work out how many of the follow up questions asked recieved a fail screening
            for(FollowUpFlow followUp : followUpList)
            {
                if(followUp.getFinalResult().equals("FAIL"))
                    totalFail++;
            }
            
            QuestionaireManager.setFailedQuestions(totalFail);
            QuestionaireManager.setFollowUpCompleted(true);
            
            //As question 13 has no corresponding follow up question must be checked
            if(currentFollowUp.getQuestionNumber() == 13)
            {
                //If it is q13, as the first branch is a result two forms cant be loaded within the same
                //thread so using Plaform.runLater to run the load of the finish scene after the other form thread has finished
                Platform.runLater(() ->{
                    StageManager.loadContentScene(StageManager.FINISH);
                });
            }
            else
                StageManager.loadContentScene(StageManager.FINISH);
        }
    }
    
    public void loadNextFollowUpPart(FlowBranchEnum branch)
    {
        //Traverses to the next node of the current nodes children depending
        //on which branch the previous question returned
        currentFollowUp.traverseTreeLevel(branch);
        
        //If the current node is a Result complete the question and move on 
        if(currentFollowUp.getCurrentNodeType() == QuestionTypeEnum.Result)
        {
            if(currentFollowUp.getCurrentNodeText().equals("PASS"))
                currentFollowUp.setFinalResult("PASS");
            else
                currentFollowUp.setFinalResult("FAIL");
            
            fIndex++;
            
            if(currentFollowUp.getQuestionNumber() == 13)
            {
                //If it is q13, as the first branch is a result two forms cant be loaded within the same
                //thread so using Plaform.runLater to run the load of the next scene after the other form thread has finished
                Platform.runLater(() ->{
                    setupFollowUpQuestion();
                });
            }
            else
                setupFollowUpQuestion();
            
        }
        else
            createNextControl();
    }
    
    private void createNextControl()
    {        
        FXMLLoader loader;
        Parent root = null;
        String controlType = "";
        
        try
        {
            //Depending on which node type the current part of the follow up is,
            //load the corresponding control, passing in the relevant variables to the control controllers.
            switch(currentFollowUp.getCurrentNodeType())
            {
                case Example :
                    loader = new FXMLLoader(getClass().getResource("/Controls/ExampleControl.fxml"));
                    root = (Parent)loader.load();
                    
                    ExampleControlController exampleControl = loader.<ExampleControlController>getController();
                    exampleControl.setFollowUpController(this);
                    exampleControl.setQuestionText(currentFollowUp.getCurrentNodeText());
                    controlType = "StageTwoExample";
                    break;
                    
                case YesNo :
                    loader = new FXMLLoader(getClass().getResource("/Controls/YesNoControl.fxml"));
                    root = (Parent)loader.load();
                    
                    YesNoControlController yesNoControl = loader.<YesNoControlController>getController();
                    yesNoControl.setFollowUpController(this);
                    yesNoControl.setQuestionText(currentFollowUp.getCurrentNodeText());
                    controlType = "StageTwoYesNo";
                    break;
                    
                case PassFail : 
                    loader = new FXMLLoader(getClass().getResource("/Controls/PassFailControl.fxml"));
                    root = (Parent)loader.load();
                    
                    PassFailControlController passFailControl = loader.<PassFailControlController>getController();
                    passFailControl.setFollowUpController(this);
                    passFailControl.setQuestionText(currentFollowUp.getCurrentNodeText(), checkListAnswers);
                    controlType = "StageTwoPassFail";
                    break;
                    
                case Checklist :
                    loader = new FXMLLoader(getClass().getResource("/Controls/ChecklistControl.fxml"));
                    root = (Parent)loader.load();
                    
                    ChecklistControlController checklistControl = loader.<ChecklistControlController>getController();
                    checklistControl.setFollowUpController(this);
                    checklistControl.setupChecklist(currentFollowUp.getCurrentNodeText());
                    controlType = "StageTwoChecklist";
                    break;
                    
                default :
                    break;
            }
            stackPaneFollowUp.getChildren().setAll(root);
            
            LanguageManager.setFormText(controlType, StageManager.getRootScene());
        }
        catch (IOException ex) 
        {
            System.out.println("Error when loading child view control - " + ex.getMessage());
        }
    }
    
    public void setChecklistAnswers(ArrayList<String> list) { checkListAnswers = list; }
    public void setCurrentNodeAnswer(String answer) { currentFollowUp.setCurrentAnswer(answer); }
    public void setQuestionHeader(String text) { lblQuestionHeader.setText(text); }
    public int getQuestionNumber() { return currentFollowUp.getQuestionNumber(); }
}
