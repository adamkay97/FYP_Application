package ControlControllers;

import Controllers.QuestionaireContentController;
import Managers.QuestionaireManager;
import Managers.SettingsManager;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

public class QuestionAudioAnswerControlController implements Initializable 
{
    @FXML private Button btnPlayback;
    @FXML private Button btnStop;
    @FXML private Button btnRecordAudio;
    @FXML private Button btnReset;
    
    private QuestionaireContentController questionaireContentController;
    
    private AudioFormat audioFormat;
    private TargetDataLine targetDataLine;
    private boolean audioSet;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) { audioSet = false; }

    @FXML public void btnYes_Action(ActionEvent event) 
    {
        if(questionaireContentController.handleYesAudioAction(audioSet))
            resetControl();
    }

    @FXML public void btnNo_Action(ActionEvent event) 
    {
        if(questionaireContentController.handleNoAudioAction(audioSet))
            resetControl();
    }

    @FXML public void btnRecordAudio_Action(ActionEvent event) 
    {
        btnRecordAudio.setDisable(true);
        btnStop.setDisable(false);
        
        audioCapture();
    }

    @FXML public void btnStop_Action(ActionEvent event) 
    {
        btnPlayback.setDisable(false);
        btnReset.setDisable(false);
        btnStop.setDisable(true);
        
        //Stop the targetLineData object from capturing data from the microphone
        //This will stop the AudioInputStream in the AudioCapture thread as they are linked
        //in the call to the AudioSystem.write method
        targetDataLine.stop();
        targetDataLine.close();
        audioSet = true;
    }

    @FXML public void btnPlayback_Action(ActionEvent event) 
    {
        File audioFile = new File(getAudioFilePath());
        
        Thread playThread = new Thread(() -> 
        {
            //Using the media and media player objects, find the file and play it back
            Media audioPlayback = new Media(audioFile.toURI().toString());
            MediaPlayer mediaPlayer = new MediaPlayer(audioPlayback);
            mediaPlayer.play();
        });
        playThread.start();
        
    }

    @FXML public void btnReset_Action(ActionEvent event) 
    {
        try 
        {
            btnRecordAudio.setDisable(false);
            btnPlayback.setDisable(true);
            btnReset.setDisable(true);

            //Find the audio file from the base path and delete it
            Path filePath = Paths.get(getAudioFilePath());
            if(Files.deleteIfExists(filePath))
                System.out.println("Successfully deleted file.");
            else
                System.out.println("Could not delete file.");
            
            audioSet = false;
        } 
        catch (IOException ex) 
        {
            System.out.println("Failed when deleting audio file on reset - " + ex.getMessage());
        }
    }

    public void setupQuestionaireController(QuestionaireContentController controller)
    {
        questionaireContentController = controller;
    }
    
    private void audioCapture()
    {
        try
        {
            //Initialise the format of how the audio is captured from the microphone
            audioFormat = getAudioFormat();
            
            //Creates the object needed for capturing the audio data
            DataLine.Info dataInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
            
            //Initilialise the targetDataLine object to read data from the microphone
            //matching the previously declared DataLine.Info object
            targetDataLine = (TargetDataLine)AudioSystem.getLine(dataInfo);
            
            //Start the thread that actually captures the audio.
            new AudioCaptureThread().start();
        }
        catch(LineUnavailableException ex)
        {
            System.out.println("Failed when initialising the objects for audio recording - " + ex.getMessage());
        }
    }
    
    private AudioFormat getAudioFormat()
    {
        //Number of samples aquired each second
        float sampleRate = 8000.0F;
        //Number of bits used for each value of the sample
        int sampleBits = 16;
        //1 channel for mono audio
        int channels = 1;
        //Description of each audio sample (either positive or postive/negative)
        boolean signed = true;
        //Describse the order in which the audi bytes are stored in the computer's memory
        boolean bigEndian = false;
        
        return new AudioFormat(sampleRate, sampleBits, channels, signed, bigEndian);
    }
    
    class AudioCaptureThread extends Thread 
    {
        @Override 
        public void run() 
        {   
            //Create directory if it doesnt exist
            File directory = new File(getAudioFileDirectory());
            if(!directory.exists())
                directory.mkdirs();
            
            //Set the file type used to be .wav
            AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;
            //Set the file location to be under audio files, then the users name and id, then childs name and then the question
            File audioFile = new File(getAudioFilePath());
            
            try
            {
                //Opens the targetDataLine object with the specified audio format,
                //then starts the line to engage in I/O 
                targetDataLine.open(audioFormat);
                targetDataLine.start();
                
                //Writes the audio from the mic as a stream of bytes to the specified file type and location specified above
                //without the need for constant looping to recieve signals
                AudioSystem.write(new AudioInputStream(targetDataLine), fileType, audioFile);
            }
            catch(IOException | LineUnavailableException ex)
            {
                System.out.println("Failed when recording audio within the capture audio thread - " + ex.getMessage());
            }
        }
    }
    
    private String getAudioFilePath()
    {
        int qIndex = QuestionaireManager.getCurrentQuestion().getQuestionId();   
        
        String audioFilePath = String.format("%s\\Question%d.wav", getAudioFileDirectory(), qIndex);
        return audioFilePath;
    }
    
    private String getAudioFileDirectory()
    {
        int childId = QuestionaireManager.getCurrentChild().getChildId();
        String childName = QuestionaireManager.getCurrentChild().getChildName();
        String childFolder = String.format("%d-%s", childId, childName);         
        
        String audioDir = String.format("%s\\%s", SettingsManager.getAudioFileLocation(), childFolder);
        return audioDir;
    }
    
    private void resetControl()
    {
        btnRecordAudio.setDisable(false);
        btnPlayback.setDisable(true);
        btnReset.setDisable(true);
        btnStop.setDisable(true);
        
        audioSet = false;
    }
}
