package Classes;

import Enums.*;
import java.util.ArrayList;
import java.util.List;

public class TreeNode
{
    private TreeNode parent;
    private List<TreeNode> children;
    private String text;
    private FlowBranchEnum branch;
    private QuestionTypeEnum type;
    
    public TreeNode() {}
    
    public TreeNode(String data, FlowBranchEnum b, QuestionTypeEnum t, TreeNode p)
    {
        parent = p;
        children = new ArrayList<>();
        
        text = data;
        branch = b;
        type = t;
    }
    
    public TreeNode addChildNode(TreeNode child)
    {
        this.children.add(child);
        child.parent = this;
        
        return child;
    }
    
    public TreeNode returnNextNode(FlowBranchEnum nextBranch)
    {
        for(TreeNode child : this.children)
        {
            if(child.getFlowBranch() == nextBranch)
                 return child;
        }
        return null;
    }
    
    public TreeNode getParent() { return parent; }
    public List<TreeNode> getChildren() { return children; }
    public String getQuestionText() { return text; }
    public FlowBranchEnum getFlowBranch() { return branch; }
    public QuestionTypeEnum getQuestionType() { return type; }
    
}
