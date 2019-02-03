package Controllers;

import Classes.*;
import Enums.FlowBranchEnum;
import Enums.QuestionTypeEnum;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;
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
        
        //followUpList.add(QuestionaireManager.getFollowUpFlow(3));
            
        setupFollowUpQuestion();
    }
    
    private void setupFollowUpQuestion()
    {
        int qNumber = currentFollowUp.getQuestionNumber();
        if(fIndex < followUpList.size())
        {
            currentFollowUp = followUpList.get(fIndex);
            String header = qNumber + " : " +
                                currentFollowUp.getCurrentNodeText().replace("#child#", 
                                    QuestionaireManager.getCurrentChild().getChildName());

            setQuestionHeader(header);

            loadNextFollowUpPart(FlowBranchEnum.Start);
        }
        else
        {
            int totalFail = 0;
            
            for(FollowUpFlow followUp : followUpList)
            {
                if(followUp.getFinalResult().equals("FAIL"))
                    totalFail++;
            }
            
            QuestionaireManager.setFailedQuestions(totalFail);
            QuestionaireManager.setFollowUpCompleted(true);
            StageManager.loadContentScene(StageManager.FINISH);
        }
    }
    
    public void loadNextFollowUpPart(FlowBranchEnum branch)
    {
        currentFollowUp.traverseTreeLevel(branch);
        
        if(currentFollowUp.getCurrentNodeType() == QuestionTypeEnum.Result)
        {
            if(currentFollowUp.getCurrentNodeText().equals("PASS"))
                currentFollowUp.setFinalResult("PASS");
            else
                currentFollowUp.setFinalResult("FAIL");
            
            fIndex++;
            setupFollowUpQuestion();
        }
        else
            createNextControl();
    }
    
    private void createNextControl()
    {        
        FXMLLoader loader = null;
        Parent root = null;
        
        try
        {
            switch(currentFollowUp.getCurrentNodeType())
            {
                case Example :
                    loader = new FXMLLoader(getClass().getResource("/Controls/ExampleControl.fxml"));
                    root = (Parent)loader.load();
                    
                    ExampleControlController exampleControl = loader.<ExampleControlController>getController();
                    exampleControl.setFollowUpController(this);
                    exampleControl.setQuestionText(currentFollowUp.getCurrentNodeText());
                    break;
                    
                case YesNo :
                    loader = new FXMLLoader(getClass().getResource("/Controls/YesNoControl.fxml"));
                    root = (Parent)loader.load();
                    
                    YesNoControlController yesNoControl = loader.<YesNoControlController>getController();
                    yesNoControl.setFollowUpController(this);
                    yesNoControl.setQuestionText(currentFollowUp.getCurrentNodeText());
                    break;
                    
                case PassFail : 
                    loader = new FXMLLoader(getClass().getResource("/Controls/PassFailControl.fxml"));
                    root = (Parent)loader.load();
                    
                    PassFailControlController passFailControl = loader.<PassFailControlController>getController();
                    passFailControl.setFollowUpController(this);
                    passFailControl.setQuestionText(currentFollowUp.getCurrentNodeText(), checkListAnswers);
                    break;
                    
                case Checklist :
                    loader = new FXMLLoader(getClass().getResource("/Controls/ChecklistControl.fxml"));
                    root = (Parent)loader.load();
                    
                    ChecklistControlController checklistControl = loader.<ChecklistControlController>getController();
                    checklistControl.setFollowUpController(this);
                    checklistControl.setupChecklist(currentFollowUp.getCurrentNodeText());
                    break;
                
                /*case Result :
                    loader = new FXMLLoader(getClass().getResource("/Controls/ResultControl.fxml"));
                    root = (Parent)loader.load();
                    
                    ResultControlController resultControl = loader.<ResultControlController>getController();
                    resultControl.setFollowUpController(this);
                    resultControl.setResultText(currentFollowUp.getCurrentNodeText());
                    break;*/
                    
                default :
                    break;
            }
            stackPaneFollowUp.getChildren().setAll(root);
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
