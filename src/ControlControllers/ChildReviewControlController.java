package ControlControllers;

import Classes.Child;
import Managers.StageManager;
import Controllers.IndividualReviewContentController;
import Managers.LanguageManager;
import Managers.LogManager;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

public class ChildReviewControlController implements Initializable 
{
    @FXML private Pane pnlLeft;
    @FXML private ImageView imgIconLeft;
    @FXML private Label txtLeftName;
    @FXML private Label txtLeftAge;
    @FXML private Label txtLeftRisk;
    private Child leftChild;
    
    @FXML private Pane pnlMiddle;
    @FXML private ImageView imgIconMid;
    @FXML private Label txtMidName;
    @FXML private Label txtMidAge;
    @FXML private Label txtMidRisk;
    private Child midChild;
    
    @FXML private Pane pnlRight;
    @FXML private ImageView imgIconRight;
    @FXML private Label txtRightName;
    @FXML private Label txtRightAge;
    @FXML private Label txtRightRisk;
    private Child rightChild;
    
    private ArrayList<Child> childList;
    
    private final LogManager logManager = new LogManager();
 
    @Override
    public void initialize(URL url, ResourceBundle rb){}    
    
    /**
     * Depending on the number of children that are to be added to the control
     * set the relevant labels and disable the un-needed controls
     */
    private void setChildControl()
    {
        switch (childList.size()) 
        {
            case 1:
                pnlRight.setDisable(true);
                pnlLeft.setDisable(true);
                pnlRight.setVisible(false);
                pnlLeft.setVisible(false);
                
                midChild = childList.get(0);
                
                txtMidName.setText(midChild.getChildName());
                txtMidAge.setText("Age: " + Integer.toString(midChild.getChildAge()));
                txtMidRisk.setText(midChild.getDiagnosisResult().getStageOneRisk());
                
                createEventHandler(imgIconMid);
                break;
            case 2:
                pnlMiddle.setDisable(true);
                pnlMiddle.setVisible(false);
                
                leftChild = childList.get(0);
                rightChild = childList.get(1);
                
                txtLeftName.setText(leftChild.getChildName());
                txtLeftAge.setText("Age: " + Integer.toString(leftChild.getChildAge()));
                txtLeftRisk.setText(leftChild.getDiagnosisResult().getStageOneRisk());
                
                txtRightName.setText(rightChild.getChildName());
                txtRightAge.setText("Age: " + Integer.toString(rightChild.getChildAge()));
                txtRightRisk.setText(rightChild.getDiagnosisResult().getStageOneRisk());
                
                createEventHandler(imgIconLeft);
                createEventHandler(imgIconRight);
                break;
            default:
                leftChild = childList.get(0);
                midChild = childList.get(1);
                rightChild = childList.get(2);
                
                txtLeftName.setText(leftChild.getChildName());
                txtLeftAge.setText("Age: " + Integer.toString(leftChild.getChildAge()));
                txtLeftRisk.setText(leftChild.getDiagnosisResult().getStageOneRisk());
                
                txtMidName.setText(midChild.getChildName());
                txtMidAge.setText("Age: " + Integer.toString(midChild.getChildAge()));
                txtMidRisk.setText(midChild.getDiagnosisResult().getStageOneRisk());
                
                txtRightName.setText(rightChild.getChildName());
                txtRightAge.setText("Age: " + Integer.toString(rightChild.getChildAge()));
                txtRightRisk.setText(rightChild.getDiagnosisResult().getStageOneRisk());
                
                createEventHandler(imgIconLeft);
                createEventHandler(imgIconMid);
                createEventHandler(imgIconRight);
                break;
        }
    }
    
    /**
     * Add event handler to the pane using lambda abstraction to create a mouse clicked event 
     * to take user to the designated child review page 
     * @param pane The control to add an event to
     */
    private void createEventHandler(ImageView imgView)
    {
        imgView.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event) -> 
        {
            Child currentChild;
            
            if(imgView.getId().equals("imgIconMid"))
                currentChild = midChild;
            else if(imgView.getId().equals("imgIconLeft"))
                currentChild = leftChild;
            else
               currentChild = rightChild;
            
            loadIndividualReviewContent(currentChild);
        });
    }
    
    private void loadIndividualReviewContent(Child currentChild)
    {
        try 
        {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(StageManager.INDIREVIEW));
            Parent root = (Parent)loader.load();
            
            IndividualReviewContentController individualReviewContent = loader.<IndividualReviewContentController>getController();
            individualReviewContent.setupIndividualReviewContent(currentChild);
            
            //Sets the review content to be loaded on the StageManager for easy access between question list
            //and question review text
            StageManager.setCurrentChildReviewParent(root);
            StageManager.loadContentSceneParent(root);
            LanguageManager.setFormText("IndividualReview", StageManager.getRootScene());
        } 
        catch (IOException ex) 
        {
            logManager.ErrorLog("Error when loading IndividualReviewContent - " + ex.getMessage());
        }
    }
    
    public void setChildList(ArrayList<Child> list) 
    { 
        childList = list;
        setChildControl();
    }
    
}
