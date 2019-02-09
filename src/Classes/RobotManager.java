package Classes;

import com.aldebaran.qi.Application;
import com.aldebaran.qi.Future;
import com.aldebaran.qi.AnyObject;
import com.aldebaran.qi.Session;

public class RobotManager 
{
    public static void main(String[] args) 
    {

        try
        {
            Application app = new Application(args);
            Session session = new Session();
            Future<Void> future = session.connect("tcp://nao.local:9559");
            future.get();
            synchronized(future) {
                future.wait(1000);
            }
            
            Object tts = null;
            tts = session.service("ALTextToSpeech");
            //tts.call("say", "Hello world");
        }
        catch(Exception ex)
        {
            System.out.println("Error connecting" + ex.getMessage());
        }
    }
    
    /*public static boolean connectToRobot()
    {
        boolean connected = true;
        
        String url = "";
        String behaviour = "";
        String[] args = {"tcp://192.168.0.102:9559"};
        try
        {
            Application app = new Application(args);
            app.start();
            //Session session = new Session();
            //Future<Void> future = session.connect(url);
            //future.get();
            //synchronized(future) {
                //future.wait(1000);
            //}
            
            //Object behaviourManager = session.service("ALBehaviourManager");
            //behaviourManager.call("runBehaviour", behaviour).get();
            
        }
        catch(Exception ex)
        {
            System.out.println("Error when connecting to NAO - " + ex.getMessage());
            connected = false;
        }
        return connected;
    }*/
}
