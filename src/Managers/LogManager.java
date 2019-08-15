package Managers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LogManager 
{
    //private final FileWriter fileWriter;
   
    private final String INFO = "%s INFO:  %s";
    private final String ERROR = "%s ERROR: %s";
    
    public LogManager()
    {
    }
    
    public void InfoLog(String logMsg) 
    {     
        try 
        {
            File file = getFile();
            FileWriter fr = new FileWriter(file, true);
            BufferedWriter br = new BufferedWriter(fr);
            br.write(String.format(INFO, getTime(), logMsg));
            br.newLine();
            br.close();
            fr.close();
        } 
        catch (IOException ex) {
            Logger.getLogger(LogManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void ErrorLog(String logMsg)
    {
        try 
        {
            File file = getFile();
            FileWriter fr = new FileWriter(file, true);
            BufferedWriter br = new BufferedWriter(fr);
            br.write(String.format(ERROR, getTime(), logMsg));
            br.newLine();
            br.close();
            fr.close();
        } 
        catch (IOException ex) {
            Logger.getLogger(LogManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public File getFile()
    {
        String dir = "Logs";
        File directory = new File(dir);
        if(!directory.exists())
            directory.mkdir();
        
        return new File(String.format("%s/Log%s.txt", dir, getDate()));
    }
    
    public String getDate()
    {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy");  
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now);
    }
    
    public String getTime()
    {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");  
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now);
    }
}
