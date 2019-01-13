package Classes;

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
    public static final String MAININFO = "/Forms/InformationContent.fxml";
    public static final String MCHATINFO = "/Forms/MCHATInfoContent.fxml";
    public static final String NAOINFO = "/Forms/NAOInfoContent.fxml";
    public static final String DETAILS = "/Forms/ChildDetailsContent.fxml";
    public static final String QUESTIONAIRE = "/Forms/QuestionaireContent.fxml";
    public static final String FINISH = "/Forms/FinishQuestionaireContent.fxml";
    public static final String POPUP = "/Forms/PopUpMessage.fxml";
    
    private static MainFormController mainFormController;
    private static User currentUser;
    
    //Offsets used for calculating where the form should be once its been dragged
    private static double offsetX = 0;
    private static double offsetY = 0;
    
    /**
     * Loads the scene passed as a parameter into
     * the main content stack pane on the main form 
     * @param fxmlPath path of the FXML scene
     */
    public static void loadContentScene(String fxmlPath)
    {
        try
        {
            mainFormController.setScene(
                FXMLLoader.load(StageManager.class.getResource(fxmlPath)
            ));
        }
        catch(IOException ex) 
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
     */
    public static void loadPopupMessage(String headerText, String messageText, ButtonTypeEnum buttonType)
    {
        try
        {
            FXMLLoader loader = new FXMLLoader(StageManager.class.getResource(POPUP));

            Parent root = (Parent)loader.load();
            Scene popup = new Scene(root);
            PopUpMessageController popupController = loader.<PopUpMessageController>getController();
            
            popupController.setPopupContent(headerText, messageText, buttonType);
            
            Stage popupStage = new Stage();
            popupStage.initStyle(StageStyle.UNDECORATED);
            popupStage.setScene(popup);
            popupStage.show();
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
    
    public static void setCurrentUser(User user) { currentUser = user; }
    public static User getCurrentUser() { return currentUser; }
}
