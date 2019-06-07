package Controllers;

import Classes.Question;
import ControlControllers.QuestionAudioAnswerControlController;
import Enums.QuestionAnswer;
import Managers.QuestionaireManager;
import Managers.RobotManager;
import Managers.SettingsManager;
import Managers.StageManager;
import ControlControllers.QuestionTextAnswerControlController;
import ControlControllers.RobotActionControlController;
import Enums.ButtonTypeEnum;
import Managers.LanguageManager;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;

public class QuestionaireContentController implements Initializable 
{
    @FXML private Label lblQuestionText;
    @FXML private Label lblQuestionHeader;
    @FXML private StackPane stkpnQuestionControl;
    @FXML private Button btnReplay;
    
    private Parent robotActionControl;
    private Parent questionAnswerControl;
    
    private boolean usesNAORobot;
    private String partIndex;
    private int qIndex;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {
        usesNAORobot = SettingsManager.getUsesNaoRobot();
        qIndex = 1;
        partIndex = "Part1";
        String questionControlPath;
        
        try
        {
            boolean usesTextArea = SettingsManager.getNoteMethod().equals("TextArea");
            
            //Load robot control and pass it the current controller and store this in a global variable 
            //for use throughout first stage of diagnosis
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Controls/RobotActionControl.fxml"));
            Parent root = (Parent)loader.load();

            RobotActionControlController robotControl = loader.<RobotActionControlController>getController();
            robotControl.setupQuestionaireController(this);
            robotActionControl = root;
            
            //Depending on the users current settings set the control required for their method of note taking
            if(usesTextArea)
                questionControlPath = "/Controls/QuestionTextAnswerControl.fxml";
            else
                questionControlPath = "/Controls/QuestionAudioAnswerControl.fxml";
            
            //Load question answer control and pass it the current controller and store this in a global variable 
            //for use throughout first stage of diagnosis
            loader = new FXMLLoader(getClass().getResource(questionControlPath));
            root = (Parent)loader.load();
            
            if(usesTextArea)
            {
                QuestionTextAnswerControlController questionControl = loader.<QuestionTextAnswerControlController>getController();
                questionControl.setupQuestionaireController(this);
            }
            else
            {
                QuestionAudioAnswerControlController questionControl = loader.<QuestionAudioAnswerControlController>getController();
                questionControl.setupQuestionaireController(this);
            }
                
            questionAnswerControl = root;
            
            //If the robot is being used set the robot control
            //Else set the question control
            if(usesNAORobot)
                setRobotControl();
            else
               setQuestionAnswerControl();
            
            //Set form text language
            LanguageManager.setFormText("StageOne", StageManager.getRootScene());          
            if(usesTextArea)
                LanguageManager.setFormText("StageOneText", StageManager.getRootScene());
            else
                LanguageManager.setFormText("StageOneAudio", StageManager.getRootScene());

            setQuestionText(qIndex);
        }
        catch(IOException ex)
        {
            System.out.println("Error when loading Robot Action Control / Question Answer Control - " + ex.getMessage());
        }
    }
    
    @FXML public void btnReplay_Action(ActionEvent event) 
    {
        handlePlayAction();
    }
    
    public void handlePlayAction() 
    {
        final String question = "Question" + Integer.toString(qIndex);
        
        if(RobotManager.getRobotConnected())
        {
            //Check if current question is question 3 as there 
            //are 2 parts to question 3's behaviour
            if(qIndex == 3)
            {
                final String qPart = question + partIndex;
                
                Thread robotThread = new Thread(() -> {
                    RobotManager.runBehaviour(qPart);
                });
                robotThread.start();
                
                //Once the first part has been run pop up the message and then wait for the
                //play button to be pressed again
                if(partIndex.equals("Part1"))
                {
                    StageManager.loadPopupMessage("Information", "Please place the fake biscuit in NAO's "
                            + "open hand now.", ButtonTypeEnum.OK);
                    partIndex = "Part2";
                }
                else
                    setQuestionAnswerControl();
            }
            else
            {
                Thread robotThread = new Thread(() -> {
                    RobotManager.runBehaviour(question);
                });
                robotThread.start();
                
                setQuestionAnswerControl();   
            }
        }
        else
        {
            String msg = "There appears to be no viable connection to NAO. Do you wish to reset the connection?";
            boolean reconnect = StageManager.loadPopupMessage("Warning", msg, ButtonTypeEnum.YESNO);
            
            if(reconnect)
                RobotManager.connectToRobot(SettingsManager.getRobotConnection());
        }
    }
    
    public boolean handleYesTextAction(String notes)
    {        
        if(checkValidNotes(notes, true))
        {
            QuestionaireManager.saveQuestionAnswer(qIndex, QuestionAnswer.YES, notes, false);
            processAnswer();
            return true;
        }
        return false;
    }
    
    public boolean handleNoTextAction(String notes) 
    {
        if(checkValidNotes(notes, false))
        {
            QuestionaireManager.saveQuestionAnswer(qIndex, QuestionAnswer.NO, notes, false);
            processAnswer();
            return true;
        }
        return false;
    }
    
    public boolean handleYesAudioAction(boolean audioSet)
    {        
        if(checkValidAudio(audioSet, true))
        {
            QuestionaireManager.saveQuestionAnswer(qIndex, QuestionAnswer.YES, "", true);
            processAnswer();
            return true;
        }
        return false;
    }
    
    public boolean handleNoAudioAction(boolean audioSet) 
    {
        if(checkValidAudio(audioSet, false))
        {
            QuestionaireManager.saveQuestionAnswer(qIndex, QuestionAnswer.NO, "", true);
            processAnswer();
            return true;
        }
        return false;
    }
    
    private void processAnswer()
    {
        if(qIndex != 20)
        {
            setQuestionText(++qIndex);
            
            //If the robot is used set the robot control after each question
            //Else just set the specified answer control
            if(usesNAORobot)
                setRobotControl();
            else
                setQuestionAnswerControl();
        }
        else
        {
            //If uses NAO robot run close behaviour
            if(usesNAORobot)
                RobotManager.runStartEnd(false);
            
            StageManager.loadContentScene(StageManager.FINISH);
        }
    }
    
    private void setRobotControl()
    {
        stkpnQuestionControl.getChildren().setAll(robotActionControl);
        btnReplay.setVisible(false);
    }
    
    private void setQuestionAnswerControl()
    {
        stkpnQuestionControl.getChildren().setAll(questionAnswerControl);
        
        //If robot is being used make the replay button visible
        if(usesNAORobot)
            btnReplay.setVisible(true);
    }
    
    /**
     * Checks to make sure the notes have some text written if the answer given 
     * indicates a risk of ASD.
     * @param notes Notes text from textArea
     * @param isYes Boolean to say whether the input has come from the Yes or No button
     * @return 
     */
    private boolean checkValidNotes(String notes, boolean isYes)
    {
        boolean valid;
        
        if(notes.equals(""))
        {
            //Checks whether the questions answer is a negative response by
            //checking against questions 2,5,12 which have alternate negative responses
            if(isYes && (qIndex == 2 || qIndex == 5 || qIndex == 12))
                valid = false;
            else if (!isYes && (qIndex != 2 && qIndex != 5 && qIndex != 12))
                valid = false;
            else
                valid = true;
        }
        else
            valid = true;
        
        //If notes arent valid display popup message to screen
        if(!valid)
        {
            String msg = "Please add notes for why you have selected this answer. "
                   + "If you have nothing further to say please input 'N/A'.";
            StageManager.loadPopupMessage("Warning", msg, ButtonTypeEnum.OK);
        }
        
        return valid;
    }
    
    /**
     * Checks to make sure some audio has been recorded if the answer given 
     * indicates a risk of ASD.
     * @param audioSet Whether any audio has actually been set
     * @param isYes Boolean to say whether the input has come from the Yes or No button
     * @return 
     */
    private boolean checkValidAudio(boolean audioSet, boolean isYes)
    {
        boolean valid;
        
        if(!audioSet)
        {
            //Checks whether the questions answer is a negative response by
            //checking against questions 2,5,12 which have alternate negative responses
            if(isYes && (qIndex == 2 || qIndex == 5 || qIndex == 12))
                valid = false;
            else if (!isYes && (qIndex != 2 && qIndex != 5 && qIndex != 12))
                valid = false;
            else
                valid = true;
        }
        else
            valid = true;
        
        //If notes arent valid display popup message to screen
        if(!valid)
        {
            String msg = "Please record audio explainng why you have selected this answer.";
            StageManager.loadPopupMessage("Warning", msg, ButtonTypeEnum.OK);
        }
        
        return valid;
    }
    
    /**
     * Sets the label text to the question stored in the QuestionaireManager
     * @param index Index of question (or question number)
     */
    private void setQuestionText(int index)
    {
        Question question = QuestionaireManager.getQuestion(index);
        lblQuestionText.setText(question.getQuestionText());
        lblQuestionHeader.setText("Question " + index + ":");
        
        //If the intructions arent null and the robot is being used, load the instructions popup
        if(question.getQuestionInstructions() != null && usesNAORobot)
        {
            if(qIndex == 6)
            {
                //Q6 do not require a picture instruction so just output the instruction to a normal popup
                //Load in seperate thread after other form threads have finished
                Platform.runLater(() -> {
                    StageManager.loadPopupMessage("Instruction", question.getQuestionInstructions(), ButtonTypeEnum.OK);
                });
            }
            else
            {
                //Load in seperate thread after other form threads have finished
                Platform.runLater(() -> {
                    StageManager.loadPopupInstruction(question.getQuestionInstructions(), qIndex);
                });
            }
        }
    }
}
