package Classes;

import Enums.FlowBranchEnum;
import Enums.QuestionTypeEnum;
import java.util.ArrayList;
import java.util.List;

public class FollowUpFlow 
{
    private TreeNode rootNode = null;
    private TreeNode currentNode = null;
    
    private int questionNumber;
    private String finalResult;
    
    public void createFollowUpFlow(List<FollowUpPart> partList, int qNumber)
    {
        TreeNode current = null;
        int flowLevel = 0;
        questionNumber = qNumber;
        
        for(FollowUpPart part : partList)
        {
            int currentLevel = part.getFlowLevel();
            String text = part.getQuestionText();
            FlowBranchEnum branch = part.getFlowBranch();
            QuestionTypeEnum type = part.getTextType();
            
            if(currentLevel == 0)
            {
                current = new TreeNode(text, branch, type, null);
                rootNode = current;
            }
            else if(flowLevel == currentLevel)
            {
                current = current.getParent().addChildNode(new TreeNode(text, branch, type, current));
            }
            else
            {
                current = current.addChildNode(new TreeNode(text, branch, type, current));
                flowLevel++;
            }
        }
        currentNode = rootNode;
    }
    
    public ArrayList<FlowBranchEnum> getAllChildrensBranches()
    {
        ArrayList<FlowBranchEnum> allBranches = new ArrayList<>();
        
        currentNode.getChildren().forEach((child) -> {
            allBranches.add(child.getFlowBranch());
        });
        return allBranches;
    }
    
    public String getFollowUpNodeResults()
    {
        //String results = "";
        TreeNode nextNode = null;
        
        
        for (TreeNode child : currentNode.getChildren()) 
        {
            if(!child.getNodeAnswer().equals(""))
                nextNode = child;
        }
        
        if(nextNode != null)
        {   
            currentNode = nextNode;
            return currentNode.getNodeAnswer() + "%" + getFollowUpNodeResults();
        }
        
        return "";
    }
    
    public void traverseTreeLevel(FlowBranchEnum branch) { currentNode = currentNode.returnNextNode(branch); }
    public void resetCurrentNode() { currentNode = rootNode; }
    
    public void setCurrentAnswer(String answer) { currentNode.setNodeAnswer(answer); }
    public void setFinalResult(String result) { finalResult = result; }
    
    public String getFinalResult() { return finalResult; }
    public QuestionTypeEnum getCurrentNodeType() { return (currentNode == null ? null : currentNode.getQuestionType()); }
    public String getCurrentNodeText() { return (currentNode == null ? "" : currentNode.getQuestionText()); }
    public int getQuestionNumber() { return questionNumber; } 
    
}
