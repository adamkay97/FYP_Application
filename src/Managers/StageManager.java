package Managers;

import Classes.User;
import Enums.ButtonTypeEnum;
import Controllers.*;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.*;

public class StageManager 
{
    //Constants for FXML form paths
    public static final String LOGIN = "/Forms/LoginForm.fxml";
    public static final String REGISTER = "/Forms/RegisterForm.fxml";
    public static final String MAIN = "/Forms/MainForm.fxml";
    public static final String INSTRUCTIONS = "/Forms/InstructionsContent.fxml";
    public static final String REVIEW = "/Forms/ReviewContent.fxml";
    public static final String INDIREVIEW = "/Forms/IndividualReviewContent.fxml";
    public static final String SETTINGS = "/Forms/SettingsContent.fxml";
    public static final String EDITQUESTIONS = "/Forms/EditQuestionsContent.fxml";
    public static final String MAININFO = "/Forms/InformationContent.fxml";
    public static final String MCHATINFO = "/Forms/MCHATInfoContent.fxml";
    public static final String NAOINFO = "/Forms/NAOInfoContent.fxml";
    public static final String DETAILS = "/Forms/ChildDetailsContent.fxml";
    public static final String QUESTIONAIRE = "/Forms/QuestionaireContent.fxml";
    public static final String FOLLOWUP = "/Forms/FollowUpContent.fxml";
    public static final String FINISH = "/Forms/FinishQuestionaireContent.fxml";
    public static final String POPUP = "/Forms/PopUpMessage.fxml";
    public static final String POPUPINSTR = "/Forms/PopUpInstructions.fxml";
    
    //Constants for FXML controls
    public static final String CHILDCONTROL = "/Control/ChildReviewControl.fxml";
    public static final String EXAMPLECONTROL = "/Control/ExampleControl.fxml";
    public static final String YESNOCONTROL = "/Control/YesNoControl.fxml";
    public static final String YNEXCONTROL = "/Control/YesNoExampleControl.fxml";
    public static final String CHECKLISTCONTROL = "/Control/ChecklistControl.fxml";
    
    private static MainFormController mainFormController;
    private static User currentUser = null;
    
    //Offsets used for calculating where the form should be once its been dragged
    private static double offsetX = 0;
    private static double offsetY = 0;
    
    private static boolean inProgress;
    private static boolean popupAnswer;
    
    //Used for when Main Form is first loaded
    private static boolean onLoad = true;
    
    //Scene for when a childs diagnosis data is being reviewed, for easy access between forms
    private static Parent currentChildReviewParent;
    
    private static Scene rootScene;
    
    /**
     * Loads the scene passed as a parameter into
     * the main content stack pane on the main form 
     * @param fxmlPath path of the FXML scene
     */
    public static void loadContentScene(String fxmlPath)
    {
        try
        {
            mainFormController.setAllScene(
                FXMLLoader.load(StageManager.class.getResource(fxmlPath)
            ));
            
            if(!onLoad)
                LanguageManager.setFormText(getFormName(fxmlPath), rootScene);
        }
        catch(IOException ex) 
        {
            System.out.println("Failed loading scene = " + ex.getMessage());
        }
    }
    
    /**
     * Loads the scene passed as a parameter into
     * the main content stack pane on the main form
     * Used when scene needs variables to be passed to the controller
     * @param root actual scene to be loaded into main form
     */
    public static void loadContentSceneParent(Parent root)
    {
        try
        {
            mainFormController.setAllScene(root);
        }
        catch(Exception ex) 
        {
            System.out.println("Failed loading scene = " + ex.getMessage());
        }
    }
    
    /**
     * Loads a new stage for a pop up message, 
     * this could be used for errors or warning messages
     * that require a simple yes no input from the user
     * 
     * @param headerText - Text for header on pop up
     * @param messageText - Text for actual message
     * @param buttonType - enum for button type that is wanted to be shown
     * @return boolean for when a Yes/No pop up is required
     */
    public static boolean loadPopupMessage(String headerText, String messageText, ButtonTypeEnum buttonType)
    {
        try
        {
            //Initialise bool for 'OK' pop up types
            popupAnswer = false;
            
            //Load popup form, pass variables to Popup controller for setting the text on the popup
            FXMLLoader loader = new FXMLLoader(StageManager.class.getResource(POPUP));
            Parent root = (Parent)loader.load();
            Scene popup = new Scene(root);
            PopUpMessageController popupController = loader.<PopUpMessageController>getController();
            popupController.setPopupContent(headerText, messageText, buttonType);
            
            Stage popupStage = new Stage();
            setFormMoveHandlers(root, popupStage);
            
            //Add event handlers for YesNo popup buttons
            if(buttonType == ButtonTypeEnum.YESNO)
            {
                popupController.btnYes.setOnAction(e -> 
                {
                    popupAnswer = true;
                    popupStage.close();
                });
                
                popupController.btnNo.setOnAction(e -> 
                {
                    popupAnswer = false;
                    popupStage.close();
                });
            }
            
            LanguageManager.setFormText("Popup", popup);
            
            //Set pop up style to Undecorated, set Modality to freeze rest of application until popup is closed
            popupStage.initStyle(StageStyle.UNDECORATED);
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.setScene(popup);
            popupStage.showAndWait();
        }
        catch(IOException ex)
        {
            System.out.println("Failed loading pop up message - " + ex.getMessage());
        }
        return popupAnswer;
    }    
    
    /**
     * Loads a new stage for a pop up instruction before a question, 
     * that will provide an image and notes on what needs to be done 
     * before the behaviour can ran
     * @param messageText Text for the message from the db
     * @param qIndex index of the question
     */
    public static void loadPopupInstruction(String messageText, int qIndex)
    {
        try
        {
            //Load popup form, pass variables to Popup controller for setting the text on the popup
            FXMLLoader loader = new FXMLLoader(StageManager.class.getResource(POPUPINSTR));
            Parent root = (Parent)loader.load();
            Scene popup = new Scene(root);
            PopUpInstructionsController popupController = loader.<PopUpInstructionsController>getController();
            popupController.setInstructionsPopupContent(messageText, qIndex);
            
            LanguageManager.setFormText("PopupInstructions", popup);
            
            Stage popupStage = new Stage();
            setFormMoveHandlers(root, popupStage);
            
            //Set pop up style to Undecorated, set Modality to freeze rest of application until popup is closed
            popupStage.initStyle(StageStyle.UNDECORATED);
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.setScene(popup);
            popupStage.showAndWait();
        }
        catch(IOException ex)
        {
            System.out.println("Failed loading pop up message - " + ex.getMessage());
        }
    }    
    
    /**
     * Loads the main stages i.e login page, registration
     * and the main form. 
     * @param formPath Path for the form to be opened
     * @param stage The current stage to be used, either stage
     * given from application.start or new Stage() for other stages
     */
    public static void loadForm(String formPath, Stage stage)
    {
        try
        {
            FXMLLoader loader = new FXMLLoader(StageManager.class.getResource(formPath));

            Parent root = (Parent)loader.load();
            Scene newScene = new Scene(root);
            
            setFormMoveHandlers(root, stage);
            
            //If the form to be loaded is the login form, call controller to create keyboard listener
            if(formPath.equals(LOGIN))
            {
                LoginFormController loginController = loader.<LoginFormController>getController();
                loginController.setKeyboardListener();
            }
            
            rootScene = newScene;
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setScene(newScene);
            stage.show();
        }
        catch(IOException ex)
        {
            System.out.println("Failed loading other form - " + ex.getMessage());
        }
    }
    
    /**
     * Using lambda expressions to set the event listener for when
     * the user drags the form so that its position is changed to where is required
     * @param root Parent of the handlers
     * @param stage Stage that is to be moved
     */
    private static void setFormMoveHandlers(Parent root, Stage stage)
    {
        root.setOnMousePressed((MouseEvent event) -> 
        {
            offsetX = event.getSceneX();
            offsetY = event.getSceneY();
        });
            
        root.setOnMouseDragged((MouseEvent event) -> 
        {
            stage.setX(event.getScreenX() - offsetX);
            stage.setY(event.getScreenY() - offsetY);
        });
    }
    
    private static String getFormName(String fxmlPath)
    {
        String formName = "";
        
        switch (fxmlPath)
        {
            case INSTRUCTIONS:
                formName = "Instructions";
                break;
            case DETAILS:
                formName = "ChildInfo";
                break;
            case QUESTIONAIRE:
                formName = "StageOne";
                break;
            case FINISH:
                formName = "FinishQuestionaire";
                break;
            case MAININFO:
                formName = "Information";
                break;
            case MCHATINFO:
                formName = "MCHATInfo";
                break;
            case NAOINFO:
                formName = "NAOInfo";
                break;
            //case SETTINGS:
                //formName = "Settings";
                //break;
            case REVIEW:
                formName = "Review";
                break;
                
        }
        
        return formName;
    }
    
    /**
     * Sets the mainFormController variable on the StageManager
     * for use when setting the content pane nested in the main form
     * @param controller 
     */
    public static void setMainFormController(MainFormController controller)
    {
        StageManager.mainFormController = controller;
    }
    
    //Used for getting the main form controller for setting values/designs on the main form 
    //from other content controllers
    public static MainFormController getMainFormController()
    {
        return StageManager.mainFormController;
    }
    
    public static void setOnLoad(boolean loaded) { onLoad = loaded; }
    public static boolean getOnLoad() { return onLoad; }
    
    public static void setInProgress(boolean start) { inProgress = start; }
    public static boolean getInProgress() { return inProgress; }
    
    public static void setCurrentUser(User user) { currentUser = user; }
    public static User getCurrentUser() { return currentUser; }
    
    public static void setCurrentChildReviewParent(Parent node) { currentChildReviewParent = node; }
    public static Parent getCurrentChildReviewParent() { return currentChildReviewParent; }
    
    public static void setRootScene(Scene scene) { rootScene = scene; }
    public static Scene getRootScene() { return rootScene; }        
}
