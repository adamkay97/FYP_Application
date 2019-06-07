package Managers;

import Classes.FormText;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTextField;
import java.util.ArrayList;
import java.util.HashMap;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

public class LanguageManager 
{
    private static String language;
    private static HashMap<String, ArrayList<FormText>> allFormText;
    
    public static void loadFormText()
    {
        DatabaseManager dbManager = new DatabaseManager();
        
        if(dbManager.connect())
        {
            allFormText = dbManager.loadFormText(language);
            dbManager.disconnect();
        }
    }
    
    public static void setFormText(String formName, Scene currentScene)
    {
        ArrayList<FormText> textList = allFormText.get(formName);

        for(FormText formText : textList)
        {
            Node node = currentScene.lookup("#"+formText.getElementId());

            if(node instanceof Label)
            {
                Label label = (Label)node;
                label.setText(formText.getText());
                continue;
            }

            if(node instanceof Button)
            {
                Button button = (Button)node;
                button.setText(formText.getText());
                continue;
            }

            if(node instanceof JFXButton)
            {
                JFXButton button = (JFXButton)node;
                button.setText(formText.getText());
                continue;
            }

            if(node instanceof JFXTextField)
            {
                JFXTextField textField = (JFXTextField)node;

                if(formText.getTextType().equals("Prompt"))
                    textField.setPromptText(formText.getText());
                else
                    textField.setText(formText.getText());
                continue;
            }

            if(node instanceof JFXRadioButton)
            {
                JFXRadioButton radButton = (JFXRadioButton)node;
                radButton.setText(formText.getText());
                continue;
            }

            if(node instanceof JFXCheckBox)
            {
                JFXCheckBox checkBox = (JFXCheckBox)node;
                checkBox.setText(formText.getText());
                continue;
            }

            if(node instanceof TextArea)
            {
                TextArea textArea = (TextArea)node;

                if(formText.getTextType().equals("Prompt"))
                    textArea.setPromptText(formText.getText());
                else
                    textArea.setText(formText.getText());
            }
        }
    }
    
    public static String getLanguage() { return language; }
    public static void setLanguage(String lang) { language = lang; }
}
