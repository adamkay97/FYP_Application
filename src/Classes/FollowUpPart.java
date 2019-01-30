package Classes;

import Enums.*;

public class FollowUpPart
{
    int questionId;
    int flowLevel;
    FlowBranchEnum flowBranch;
    String questionText;
    QuestionTypeEnum questionType;
    
    public FollowUpPart(int qId, int level, String branch, String text, String type)
    {
        questionId = qId;
        flowLevel = level;
        questionText = text;
        
        flowBranch = parseBranch(branch);
        questionType = parseType(type);
    }
    
    private FlowBranchEnum parseBranch(String branch)
    {
        FlowBranchEnum temp = null;
        switch(branch)
        {
            case "Root": temp = FlowBranchEnum.Root; break;
            case "Start": temp = FlowBranchEnum.Start; break;
            case "Yes": temp = FlowBranchEnum.Yes; break;
            case "No": temp = FlowBranchEnum.No; break;
            case "Both": temp = FlowBranchEnum.Both; break;    
            case "YesAny": temp = FlowBranchEnum.YesAny; break;
            case "YesOnly": temp = FlowBranchEnum.YesOnly; break;
            case "Yes2OM": temp = FlowBranchEnum.Yes2OM; break;
            case "Yes1O": temp = FlowBranchEnum.Yes1O; break;
            case "PassOnly": temp = FlowBranchEnum.PassOnly; break;
            case "Pass": temp = FlowBranchEnum.Pass; break;
            case "NoAny": temp = FlowBranchEnum.NoAny; break;
            case "NoOnly": temp = FlowBranchEnum.NoOnly; break;
            case "No1ON": temp = FlowBranchEnum.No1ON; break;
            case "FailOnly": temp = FlowBranchEnum.FailOnly; break;
            case "Fail": temp = FlowBranchEnum.Fail; break;
        }
        return temp;
    }
    
    private QuestionTypeEnum parseType(String type)
    {
        QuestionTypeEnum temp = null;
        
        switch(type)
        {
            case "Question": temp = QuestionTypeEnum.Question; break;
            case "PassFail": temp = QuestionTypeEnum.PassFail; break;
            case "YesNo": temp = QuestionTypeEnum.YesNo; break;
            case "Example": temp = QuestionTypeEnum.Example; break;
            case "Checklist": temp = QuestionTypeEnum.Checklist; break;
            case "Result": temp = QuestionTypeEnum.Result; break;
        }
        return temp;
    }
    
    public int getQuestionId() { return questionId; }
    public int getFlowLevel() { return flowLevel; }
    public FlowBranchEnum getFlowBranch() { return flowBranch; }
    public String getQuestionText() { return questionText; }
    public QuestionTypeEnum getTextType() { return questionType; }
}
