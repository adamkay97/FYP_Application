package Controllers;

import Classes.StageManager;
import Enums.ButtonTypeEnum;
import com.jfoenix.controls.*;
import com.jfoenix.transitions.hamburger.*;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
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
    @FXML private Pane pnlMenuContent;
    @FXML private Pane pnlTitle;
    @FXML private Pane pnlMenuButtons;
    @FXML private ImageView imgNaoIcon;
    @FXML private JFXButton btnClose;
    @FXML private JFXButton btnMinimize;
    @FXML private JFXButton btnMenuStart;
    @FXML private JFXButton btnMenuInfo;
    @FXML private JFXButton btnMenuReview;
    @FXML private JFXButton btnMenuSettings;
    @FXML private JFXHamburger btnMenuCollapse;
    @FXML private JFXDrawer menuDrawer;
    
    private final int MENU_SIZE = 288;
    private String currentPage;
    private HamburgerBackArrowBasicTransition collapseTransition;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {
        System.out.println("Main Form Initialize");
        
        initializeSideMenu();
        
        StageManager.setMainFormController(this);
        StageManager.loadContentScene(StageManager.INSTRUCTIONS);
        currentPage = "Start";
    } 
    
    //FXML Actions
    @FXML private void btnMenuStart_Action(ActionEvent event)
    {
        StageManager.loadContentScene(StageManager.INSTRUCTIONS);
        setSelectedMenuButton("Start");
    }
    
    @FXML private void btnMenuInfo_Action(ActionEvent event)
    {
        StageManager.loadContentScene(StageManager.MAININFO);
        setSelectedMenuButton("Info");
    }
    
    @FXML private void btnMenuReview_Action(ActionEvent event)
    {
        StageManager.loadContentScene(StageManager.REVIEW);
        setSelectedMenuButton("Review");
    }
    
    @FXML private void btnMenuSettings_Action(ActionEvent event)
    {
        StageManager.loadContentScene(StageManager.SETTINGS);
        setSelectedMenuButton("Settings");
    }
    
    @FXML private void btnLogout_Action(ActionEvent event)
    {
        //StageManager.loadPopupMessage("Warning", "Are you sure you want to logout? If you are "
                //+ "part way through the diagnosis your progress will not be saved.", ButtonTypeEnum.YESNO);
        
        //TODO: If yes then:
        quitMainForm();
        StageManager.loadForm(StageManager.LOGIN, new Stage());
    }
    
    @FXML private void btnMenuQuit_Action(ActionEvent event) { quitMainForm(); }
    @FXML private void btnMinimize_Click(ActionEvent event) { minimizeMainForm(); }
    @FXML private void btnQuit_Click(ActionEvent event) { quitMainForm(); }
    
    /**
     * Replaces the current scene in the Main
     * Content stack pane.
     * 
     * @param node the scene that is to replace the current scene
     */
    public void setScene(Node node)
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
        collapseTransition.setRate(collapseTransition.getRate() * -1);
        collapseTransition.play();
        
        //Add extra check for when function is called outside of Main controller
        if(!menuDrawer.isOpened())
        {
            menuDrawer.open();
            menuDrawer.toFront();
            setControlsVisible(true);
            pnlMenuContent.setTranslateX(0);
            pnlMainContentAnchor.setPrefWidth(pnlMainContentAnchor.getPrefWidth()- MENU_SIZE);
            pnlMainContentAnchor.setLayoutX(pnlMainContentAnchor.getLayoutX() + MENU_SIZE);
        }
    }
    
    public void closeMenuDrawer()
    {
        collapseTransition.setRate(collapseTransition.getRate() * -1);
        collapseTransition.play();
        
        //Add extra check for when function is called outside of Main controller
        if(menuDrawer.isOpened())
        {
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
   
    private void quitMainForm() 
    {
        Stage currentStage = (Stage)mainAnchorPane.getScene().getWindow();
        currentStage.close();
    }
}
