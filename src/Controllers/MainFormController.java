package Controllers;

import Classes.PopupText;
import Managers.QuestionaireManager;
import Managers.StageManager;
import Enums.ButtonTypeEnum;
import Managers.LanguageManager;
import Managers.SettingsManager;
import com.jfoenix.controls.*;
import com.jfoenix.transitions.hamburger.*;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.*;

/**
 *
 * @author Adam
 */
public class MainFormController implements Initializable 
{
    //FXML Variables
    @FXML private AnchorPane mainAnchorPane;
    @FXML private AnchorPane pnlMainContentAnchor;
    @FXML private StackPane pnlMainContent;
    @FXML private VBox vboxMenu;
    @FXML private Pane pnlMenuContent;
    @FXML private Pane pnlTitle;
    @FXML private Pane pnlMenuButtons;
    @FXML private ImageView imgNaoIcon;
    @FXML private ImageView maxResIcon;
    @FXML private JFXButton btnMinimize;
    @FXML private JFXButton btnMenuStart;
    @FXML private JFXButton btnMenuInfo;
    @FXML private JFXButton btnMenuReview;
    @FXML private JFXButton btnMenuSettings;
    @FXML private JFXHamburger btnMenuCollapse;
    @FXML private JFXDrawer menuDrawer;
    @FXML private Label lblMainSetName;
    
    private final int MENU_SIZE = 288;
    private HamburgerBackArrowBasicTransition collapseTransition;
    
    private double DEFAULTX;
    private double DEFAULTY;
    private double DEFAULTHEIGHT;
    private double DEFAULTWIDTH;
    
    private String currentPage;
    private boolean maximized = false;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {
        initializeSideMenu();
        
        StageManager.setMainFormController(this);
        StageManager.loadContentScene(StageManager.INSTRUCTIONS);
        currentPage = "Start";
        
        //Run in seperate thread after form has been loaded in order to get access
        //to elements that are not available within 'intitialize'.
        Platform.runLater(() ->{
            LanguageManager.setFormText("Main", StageManager.getRootScene());
            LanguageManager.setFormText("Instructions", StageManager.getRootScene());
            
            StageManager.setOnLoad(false);
            
            Stage mainStage = (Stage)mainAnchorPane.getScene().getWindow();
            
            DEFAULTX = mainStage.getX();
            DEFAULTY = mainStage.getY();
            DEFAULTHEIGHT = mainStage.getHeight();
            DEFAULTWIDTH = mainStage.getWidth();
        });
    } 
    
    //FXML Actions
    @FXML public void btnMenuStart_Action(ActionEvent event)
    {
        if(allowFormChange())
        {
            StageManager.loadContentScene(StageManager.INSTRUCTIONS);
            setSelectedMenuButton("Start");
        }
    }
    
    @FXML public void btnMenuInfo_Action(ActionEvent event)
    {
        if(allowFormChange())
        {
            StageManager.loadContentScene(StageManager.MAININFO);
            setSelectedMenuButton("Info");
        }
    }
    
    @FXML public void btnMenuReview_Action(ActionEvent event)
    {
        if(allowFormChange())
        {
            StageManager.loadContentScene(StageManager.REVIEW);
            setSelectedMenuButton("Review");
        }
    }
    
    @FXML public void btnMenuSettings_Action(ActionEvent event)
    {
        if(allowFormChange())
        {
            StageManager.loadContentScene(StageManager.SETTINGS);
            setSelectedMenuButton("Settings");
            
            //Run after the form has been loaded in seperate thread as running from within
            //same thread in 'loadContentScene' doesn't work.
            Platform.runLater(() -> {
                LanguageManager.setFormText("Settings", StageManager.getRootScene());
            });
        }
    }
    
    @FXML public void btnLogout_Action(ActionEvent event)
    {
        if(allowFormChange())
        {
            quitMainForm();
            StageManager.loadForm(StageManager.LOGIN, new Stage());
        }
    }
    
    @FXML public void btnMenuQuit_Action(ActionEvent event) 
    { 
        if(allowFormChange())
            quitMainForm(); 
    }
    
    @FXML public void btnQuit_Click(ActionEvent event) 
    { 
        if(allowFormChange())
            quitMainForm(); 
    }
    
    @FXML public void btnMinimize_Click(ActionEvent event) { minimizeMainForm(); }
    @FXML public void btnMaxRes_Action(ActionEvent event) { maximizeMainForm(); }
    
    /**
     * Replaces all of the current scenes in the Main
     * Content stack pane.
     * 
     * @param node the scene that is to replace the current scene
     */
    public void setAllScene(Node node)
    {
        pnlMainContent.getChildren().setAll(node);
    }
    
    /**
     * Initialise the Side menu with JFXHamburger transition
     * and add JFXDrawer logic for collapsible menu.
     */
    private void initializeSideMenu()
    {
        pnlMainContent.prefWidthProperty().bind(pnlMainContentAnchor.widthProperty());
        pnlMainContent.prefHeightProperty().bind(pnlMainContentAnchor.heightProperty());
        menuDrawer.setSidePane(pnlMenuContent);
        menuDrawer.open();
        
        //Create transition and sets initial state to close icon
        collapseTransition = new HamburgerBackArrowBasicTransition(btnMenuCollapse);
        collapseTransition.setRate(1);
        collapseTransition.play();
        
        //Adds event for when the hamburger is pressed, when it is 
        //pressed it will multipply the current state by -1 to get the opposite state
        btnMenuCollapse.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> 
        {
            if(menuDrawer.isOpened())
                closeMenuDrawer();
            else
                openMenuDrawer();
        });
    }
    
    public void openMenuDrawer()
    {
        //Add extra check for when function is called outside of Main controller
        if(!menuDrawer.isOpened())
        {
            collapseTransition.setRate(collapseTransition.getRate() * -1);
            collapseTransition.play();
        
            menuDrawer.open();
            menuDrawer.toFront();
            setControlsVisible(true);
            pnlMenuContent.setTranslateX(0);
            pnlMainContentAnchor.setPrefWidth(pnlMainContentAnchor.getPrefWidth()- MENU_SIZE + 5);
            pnlMainContentAnchor.setLayoutX(pnlMainContentAnchor.getLayoutX() + MENU_SIZE);
        }
    }
    
    public void closeMenuDrawer()
    {
        //Add extra check for when function is called outside of Main controller
        if(menuDrawer.isOpened())
        {
            collapseTransition.setRate(collapseTransition.getRate() * -1);
            collapseTransition.play();
            
            menuDrawer.close();
            menuDrawer.toBack();
            setControlsVisible(false);
            pnlMenuContent.setTranslateX(-MENU_SIZE);
            pnlMainContentAnchor.setPrefWidth(pnlMainContentAnchor.getPrefWidth() + MENU_SIZE - 5);
            pnlMainContentAnchor.setLayoutX(pnlMainContentAnchor.getLayoutX() - MENU_SIZE);
        }
    }
    
    /**
     * Underlines the button for the page passed in and
     * removes the underline from the previous page button
     * @param page name of base page for underlining
     */
    public void setSelectedMenuButton(String page)
    {
        switch(currentPage)
        {
            case "Start" :
                btnMenuStart.setUnderline(false);
                break;
            case "Info" :
                btnMenuInfo.setUnderline(false);
                break;
            case "Review" :
                btnMenuReview.setUnderline(false);
                break;
            case "Settings" :
                btnMenuSettings.setUnderline(false);
                break;
        }
        
        switch(page)
        {
            case "Start" :
                btnMenuStart.setUnderline(true);
                break;
            case "Info" :
                btnMenuInfo.setUnderline(true);
                break;
            case "Review" :
                btnMenuReview.setUnderline(true);
                break;
            case "Settings" :
                btnMenuSettings.setUnderline(true);
                break;
        }
        
        currentPage = page;
    }
    
    private boolean allowFormChange()
    {
        boolean allow = true;
        
        //Checks to see if a diagnosis is in progress
        if(StageManager.getInProgress())
        {
            //If it is ask for confirmation to leave as progress is not saved
            PopupText popup = LanguageManager.getPopupText(7);
            allow = StageManager.loadPopupMessage(popup.getHeader(), popup.getMessage(), ButtonTypeEnum.YESNO);
            
            //If they select yes to leave, reset Question maps on manager.
            if(allow)
            {
                QuestionaireManager.resetQuestionaireManager();
                SettingsManager.deleteCurrentAudioFiles();
                StageManager.setInProgress(false);
            }
        }
        return allow;
    }
     
    private void setControlsVisible(Boolean visible)
    {
        pnlTitle.setVisible(visible);
        pnlMenuButtons.setVisible(visible);
        imgNaoIcon.setVisible(visible);
    }
    
    private void minimizeMainForm()
    {
        Stage currentStage = (Stage)btnMinimize.getScene().getWindow();
        currentStage.setIconified(true);
    }
    
    private void maximizeMainForm()
    {
        Stage mainStage = (Stage)mainAnchorPane.getScene().getWindow();
        
        if(!maximized)
        {
            maxResIcon.getStyleClass().clear();
            maxResIcon.getStyleClass().add("restore");
            
            Screen screen = Screen.getPrimary();
            Rectangle2D bounds = screen.getVisualBounds();

            mainStage.setX(bounds.getMinX());
            mainStage.setY(bounds.getMinY());
            mainStage.setWidth(bounds.getWidth());
            mainStage.setHeight(bounds.getHeight());
            
            double size = bounds.getWidth() - pnlMenuContent.getWidth();
            pnlMainContentAnchor.setPrefWidth(size);
            pnlMenuContent.setPrefHeight(bounds.getHeight()-6);
            vboxMenu.setPrefHeight(bounds.getHeight()-6);
            
            maximized = true;
        }
        else
        {
            maxResIcon.getStyleClass().clear();
            maxResIcon.getStyleClass().add("maximize");
            
            mainStage.setX(DEFAULTX);
            mainStage.setY(DEFAULTY);
            mainStage.setWidth(DEFAULTWIDTH);
            mainStage.setHeight(DEFAULTHEIGHT);
            
            double size = DEFAULTWIDTH - pnlMenuContent.getWidth();
            pnlMainContentAnchor.setPrefWidth(size);
            pnlMenuContent.setPrefHeight(DEFAULTHEIGHT-6);
            vboxMenu.setPrefHeight(DEFAULTHEIGHT-6);
            maximized = false;
        }
    }
   
    private void quitMainForm() 
    {
        Stage currentStage = (Stage)mainAnchorPane.getScene().getWindow();
        currentStage.close();
    }
}
