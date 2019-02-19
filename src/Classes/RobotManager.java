package Classes;

import com.aldebaran.qi.Application;
import com.aldebaran.qi.helper.proxies.ALBehaviorManager;

public class RobotManager 
{
    private static Application application;
    private static boolean connected;
    
    public static boolean connectToRobot()
    {
        connected = true;
        
        try
        {
           //Pass empty args and robot URL to NAOqi Application type
           //To initiate connecting to NAO.
           String[] args = {""};
           String robotUrl = getNaoConnectionURL();
           application = new Application(args, robotUrl);
             
           // Start your application
           application.start(); 
        }
        catch(Exception ex)
        {
            System.out.println("Error when connecting to NAO - " + ex.getMessage());
            connected = false;
        }
        return connected;
    }
    
    public static void runBehaviour(String question)
    {
        try
        {
            ALBehaviorManager behMan = new ALBehaviorManager(application.session());
            behMan.stopAllBehaviors();
            String test = behMan.resolveBehaviorName("Questions/"+question);
            behMan.runBehavior(test);
        }
        catch(Exception ex)
        {
            System.out.println("Error when running behaviour for question " + 
                    question + " - " + ex.getMessage());
        }
    }
    
    private static String getNaoConnectionURL()
    {
        DatabaseManager dbManager = new DatabaseManager();
        String connectURL = "";
        
        if(dbManager.connect())
        {
            connectURL = dbManager.getNaoConnectionURL();
            dbManager.disconnect();
        }
        
        return connectURL;
    }
    
    public boolean getRobotConnected() { return connected; }
}
