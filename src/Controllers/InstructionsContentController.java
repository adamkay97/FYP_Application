/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers;

import Classes.StageManager;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.*;

/**
 * FXML Controller class
 *
 * @author Adam
 */
public class InstructionsContentController implements Initializable 
{
    @FXML private Label lblInstructions;
    @FXML private ScrollPane scrlPaneInstr;

    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {
        //scrlPaneInstr.setContent(lblInstructions);
        //lblInstructions.prefWidthProperty().bind(scrlPaneInstr.widthProperty());
        //lblInstructions.setVisible(true);
    }    
    
    @FXML private void btnContinue_Action(ActionEvent event)
    {
        StageManager.loadContentScene(StageManager.DETAILS);
    }
}
