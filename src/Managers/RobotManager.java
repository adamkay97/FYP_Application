package Managers;

import com.aldebaran.qi.Application;
import com.aldebaran.qi.helper.proxies.ALBehaviorManager;

public class RobotManager 
{
    private static Application application;
    private static boolean connected;
    
    private static final LogManager logManager = new LogManager();
    
    public static boolean connectToRobot(String connectionURL)
    {
        connected = false;
        
        try
        {
            //Pass empty args and robot URL to NAOqi Application type
            //To initiate connecting to NAO.
            String[] args = {""};
           
            application = new Application(args, connectionURL);
            // Start your application
            application.start(); 
            connected = true;
        }
        catch(Exception ex)
        {
            logManager.ErrorLog("Error when connecting to NAO - " + ex.getMessage());
            application.stop();
            System.gc();
        }
        return connected;
    }
    
    public static void runStartEnd(boolean start)
    {
        try
        {
            ALBehaviorManager behMan = new ALBehaviorManager(application.session());
            behMan.stopAllBehaviors();
            String behaviourName;

            if(start)
                behaviourName = behMan.resolveBehaviorName("Start");
            else
                behaviourName = behMan.resolveBehaviorName("Close");

            behMan.runBehavior(behaviourName);
        }
        catch(Exception ex)
        {
            logManager.ErrorLog("Error when running start or end behaviour");
        }
    }
    
    public static void runBehaviour(String question)
    {
        try
        {
            ALBehaviorManager behMan = new ALBehaviorManager(application.session());
            behMan.stopAllBehaviors();
            String behaviourName = behMan.resolveBehaviorName("Questions/"+question);
            behMan.runBehavior(behaviourName);
        }
        catch(Exception ex)
        {
            logManager.ErrorLog("Error when running behaviour for question " + 
                    question + " - " + ex.getMessage());
        }
    }
    
    public static boolean getRobotConnected() { return connected; }
}
