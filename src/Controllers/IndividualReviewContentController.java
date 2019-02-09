package Controllers;

import Classes.DatabaseManager;
import Classes.ReviewData;
import Classes.StageManager;
import ControlControllers.QuestionReviewControlController;
import ControlControllers.YesNoExampleControlController;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class IndividualReviewContentController implements Initializable 
{
    @FXML private Label lblReviewHeader; 
    @FXML private VBox vboxReviewControls;
    
    @Override
    public void initialize(URL url, ResourceBundle rb){}    
    
    public void setupIndividualReviewContent(int childId, String childName)
    {
        DatabaseManager dbManager = new DatabaseManager();
        ArrayList<ReviewData> reviewData;
        
        String header = lblReviewHeader.getText().replace("#child#", childName);
        lblReviewHeader.setText(header);
        
        if(dbManager.connect())
        {
            int userId = StageManager.getCurrentUser().getUserId();
            reviewData = dbManager.loadReviewData(childId, userId);
            
            loadQuestionReviewControls(reviewData);
        }
    }
    
    private void loadQuestionReviewControls(ArrayList<ReviewData> reviewData)
    {
        FXMLLoader loader;
        Parent root;
        try
        {
            for(ReviewData data : reviewData)
            {
                //Load control and pass it the text
                loader = new FXMLLoader(getClass().getResource("/Controls/QuestionReviewControl.fxml"));
                root = (Parent)loader.load();

                QuestionReviewControlController reviewControl = loader.<QuestionReviewControlController>getController();
                reviewControl.setUpReviewControl(data);
                
                vboxReviewControls.getChildren().add(root);
            }
        }
        catch(IOException ex)
        {
            System.out.println("Error when loading QuestionReviewControlController - " + ex.getMessage());
        }
    }
    
    public void btnBack_Action(ActionEvent event)
    {
        StageManager.loadContentScene(StageManager.REVIEW);
    }
}
