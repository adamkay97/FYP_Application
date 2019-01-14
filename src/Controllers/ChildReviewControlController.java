package Controllers;

import Classes.Child;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

public class ChildReviewControlController implements Initializable 
{
    @FXML private Pane pnlLeft;
    @FXML private Label txtLeftName;
    @FXML private Label txtLeftAge;
    @FXML private Label txtLeftRisk;
    private Child leftChild;
    
    @FXML private Pane pnlMiddle;
    @FXML private Label txtMidName;
    @FXML private Label txtMidAge;
    @FXML private Label txtMidRisk;
    private Child midChild;
    
    @FXML private Pane pnlRight;
    @FXML private Label txtRightName;
    @FXML private Label txtRightAge;
    @FXML private Label txtRightRisk;
    private Child rightChild;
    
    private ArrayList<Child> childList;
 
    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {
    }    
    
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
                txtMidRisk.setText(midChild.getResultText());
                
                createEventHandler(pnlMiddle);
                break;
            case 2:
                pnlMiddle.setDisable(true);
                pnlMiddle.setVisible(false);
                
                leftChild = childList.get(0);
                rightChild = childList.get(1);
                
                txtLeftName.setText(leftChild.getChildName());
                txtLeftAge.setText("Age: " + Integer.toString(leftChild.getChildAge()));
                txtLeftRisk.setText(leftChild.getResultText());
                
                txtRightName.setText(rightChild.getChildName());
                txtRightAge.setText("Age: " + Integer.toString(rightChild.getChildAge()));
                txtRightRisk.setText(rightChild.getResultText());
                
                createEventHandler(pnlLeft);
                createEventHandler(pnlRight);
                break;
            default:
                leftChild = childList.get(0);
                midChild = childList.get(1);
                rightChild = childList.get(2);
                
                txtLeftName.setText(leftChild.getChildName());
                txtLeftAge.setText("Age: " + Integer.toString(leftChild.getChildAge()));
                txtLeftRisk.setText(leftChild.getResultText());
                
                txtMidName.setText(midChild.getChildName());
                txtMidAge.setText("Age: " + Integer.toString(midChild.getChildAge()));
                txtMidRisk.setText(midChild.getResultText());
                
                txtRightName.setText(rightChild.getChildName());
                txtRightAge.setText("Age: " + Integer.toString(rightChild.getChildAge()));
                txtRightRisk.setText(rightChild.getResultText());
                
                createEventHandler(pnlLeft);
                createEventHandler(pnlMiddle);
                createEventHandler(pnlRight);
                break;
        }
    }
    
    /**
     * Add event handler to the pane using lambda abstraction to create a mouse clicked event 
     * to take user to the designated child review page 
     * @param pane The control to add an event to
     */
    private void createEventHandler(Pane pane)
    {
        //pane.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event) -> {
            
        //});
    }
    
    public void setChildList(ArrayList<Child> list) 
    { 
        childList = list;
        setChildControl();
    }
    
}
