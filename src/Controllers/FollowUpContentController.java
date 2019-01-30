package Controllers;

import Classes.*;
import Enums.FlowBranchEnum;
import Enums.QuestionTypeEnum;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
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
    private int fIndex;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {
        ArrayList<Integer> flaggedQuestions = QuestionaireManager.getFlaggedQuestions();
        followUpList = new ArrayList<>();
        fIndex = 0;
        
        //Initialize followUpList with the FollowUpFlow of each flagged questions
        for (int i : flaggedQuestions)
            followUpList.add(QuestionaireManager.getFollowUpFlow(i));
        
        currentFollowUp = followUpList.get(fIndex);
        String header = currentFollowUp.getCurrentNodeText().replace("#child#", 
                QuestionaireManager.getCurrentChild().getChildName());
        
        setQuestionHeader(header);
        loadNextFollowUpPart(FlowBranchEnum.Start);
    }    
    
    public void loadNextFollowUpPart(FlowBranchEnum branch)
    {
        currentFollowUp.traverseTreeLevel(branch);
        createNextControl();
    }
    
    public ArrayList<FlowBranchEnum> getFollowingBranches()
    {
        return currentFollowUp.getAllChildrensBranches();
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
                    break;
                case Checklist :
                    loader = new FXMLLoader(getClass().getResource("/Controls/ChecklistControl.fxml"));
                    root = (Parent)loader.load();
                    
                    ChecklistControlController checklistControl = loader.<ChecklistControlController>getController();
                    checklistControl.setFollowUpController(this);
                    checklistControl.setupChecklist(currentFollowUp.getCurrentNodeText());
                    break;
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
    
    public void setQuestionHeader(String text) { lblQuestionHeader.setText(text); }
}
