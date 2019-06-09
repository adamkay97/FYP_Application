package Controllers;

import ControlControllers.ChildReviewControlController;
import Classes.Child;
import Managers.DatabaseManager;
import Managers.LanguageManager;
import Managers.StageManager;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class ReviewContentController implements Initializable 
{
    @FXML private ImageView btnLeftArrow;
    @FXML private ImageView btnRightArrow;
    @FXML private StackPane stackPaneChildView;
    @FXML private Label lblNoCases;
    
    private ArrayList<Parent> childViewControls;
    private int controlIndex;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {
        DatabaseManager dbManager = new DatabaseManager();
        childViewControls = new ArrayList<>();
        controlIndex = 0;
   
        if(dbManager.connect())
        {
            //Load children from the database to be added to the review control 
            ArrayList<Child> childList = dbManager.loadChildren();
            dbManager.disconnect();
            
            //Runs below in a seperate thread, adding the work to the FX queue
            //Means form can be loaded straight away without waiting for controls to load
            Platform.runLater(() -> 
            {
                loadChildView(childList);

                //If a control exists add the first one to the stack;
                if(childViewControls.size() > 0)
                    stackPaneChildView.getChildren().add(childViewControls.get(controlIndex));

                setArrowButtonEvents();
            });
        }
    }    
    
    /**
     * Creates the child view control 
     * @param childList List of all children that can be reviewed by user
     */
    private void loadChildView(ArrayList<Child> childList)
    {
        //Checks to see if there are actually any children that
        //need to be added to the view control
        if(childList.isEmpty())
        {
            lblNoCases.setVisible(true);
            return;
        }
            
        //If the list is less than 3 only need one control
        if(childList.size() <= 3)
            createChildControl(childList);
        else
        {
            ArrayList<Child> shortList = new ArrayList<>();
            
            //Enable arrows for when there are more than 3 children
            setArrowVisible(true, btnLeftArrow);
            setArrowVisible(true, btnRightArrow);
            
            //Else for each child in the list add to a shorter list that will only ever have a max size of 3
            for (Child child : childList)
            {
                shortList.add(child);

                //When the list is at its max pass to the control creater and then empty the list
                if(shortList.size() == 3)
                {
                    createChildControl(shortList);
                    shortList = new ArrayList<>();
                }
            }
            //If there are any children left that need to be added to the controls add them
            if(shortList.size() > 0)
                createChildControl(shortList);   
        }
    }
    
    /**
     * Create a new child view control for each set of 3 or less children
     * Add them to the stack pane so that they can be cycled through using the arrow keys
     * if necessary.
     * @param list The list of max 3 children that are to be added to the control
     */
    private void createChildControl(ArrayList<Child> list)
    {
        try 
        {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Controls/ChildReviewControl.fxml"));
            Parent root = (Parent)loader.load();
            
            ChildReviewControlController childControl = loader.<ChildReviewControlController>getController();
            childControl.setChildList(list);
            
            childViewControls.add(root);
        } 
        catch (IOException ex) 
        {
            System.out.println("Error when loading child view control - " + ex.getMessage());
        }
        
    }
    
    private void setArrowButtonEvents()
    {
        //Adds event handler for most click event to the left arrow
        btnLeftArrow.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event) -> 
        {
            //Only runs if the control index is greater than 0;
            if(controlIndex > 0)
            {
                controlIndex--;
                setReviewControlAnimation(controlIndex, true);
            }
            
            //If there are no controls to the left disable the left arrow
            if(controlIndex == 0)
                setArrowVisible(false, btnLeftArrow);
            else
                setArrowVisible(true, btnLeftArrow);
            
            setArrowVisible(true, btnRightArrow);
        });
        
        //Adds event handler for most click event to the left arrow
        btnRightArrow.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event) -> 
        {
            //Only tuns if the control index is less than the amount of controls that have been created
            if(controlIndex < childViewControls.size()-1)
            {
                controlIndex++;
                setReviewControlAnimation(controlIndex, false);
            }
            
            //If there are no controls to the right disable the right arrow
            if(controlIndex == childViewControls.size()-1)
                setArrowVisible(false, btnRightArrow);
            else
                setArrowVisible(true, btnRightArrow);
            
            setArrowVisible(true, btnLeftArrow);
        });
    }
    
    /**
     * Sets the animation on the controls to give the effect of one
     * control coming in from the left and one disappearing to the right or vice versa
     * @param index The current control index that the stackPane should be showing
     * @param leftArrow Whether this has come from a leftArrow click or a right arrow click
     */
    private void setReviewControlAnimation(int index, boolean leftArrow)
    {
        Parent prevControl;
        double outWidth = 0;
        Parent nextControl = childViewControls.get(index);
        
        //If the click is from the left arrow set up the following variables for the left animation
        //Else set them to the values needed for right animation
        if(leftArrow)
        {
            nextControl.translateXProperty().set(stackPaneChildView.getWidth());
            prevControl = childViewControls.get(index+1);
            outWidth = stackPaneChildView.getWidth() * -1;
        }
        else
        {
            nextControl.translateXProperty().set(stackPaneChildView.getWidth() * -1);
            prevControl = childViewControls.get(index-1);
            outWidth = stackPaneChildView.getWidth();
        }
        
        stackPaneChildView.getChildren().add(nextControl);

        Timeline animTimeline = new Timeline();
        
        //Uses 2 seperate KeyFrames with opposite translate directions
        KeyValue kv1 = new KeyValue(nextControl.translateXProperty(), 0, Interpolator.EASE_IN);
        KeyFrame kf1 = new KeyFrame(Duration.seconds(0.75), kv1);
        KeyValue kv2 = new KeyValue(prevControl.translateXProperty(), outWidth, Interpolator.EASE_IN);
        KeyFrame kf2 = new KeyFrame(Duration.seconds(0.75), kv2);
        
        animTimeline.getKeyFrames().addAll(kf1, kf2);
        
        //When the animation has finished remove the previous control from the parent stack pane
        animTimeline.setOnFinished(fEvent -> 
        {
            int removeIndex = leftArrow ? index+1 : index-1;
            stackPaneChildView.getChildren().remove(childViewControls.get(removeIndex));
        });
        animTimeline.play();
        
        
    }
    
    private void setArrowVisible(boolean visible, ImageView arrow)
    {
        arrow.setDisable(!visible);
        arrow.setVisible(visible);
    }
}
