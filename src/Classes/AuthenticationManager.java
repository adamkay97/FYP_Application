package Classes;

import Enums.ButtonTypeEnum;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.HashMap;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;


public class AuthenticationManager 
{
    //The algorithm string used for PBKDF2 hashing
    private final String HASHALGORITHM = "PBKDF2WithHmacSHA1";
    
    public boolean authenticateLogin(String username, String password)
    {
        DatabaseManager dbManager = new DatabaseManager();
        HashMap<String, User> userMap = new HashMap<>();
        
        if(dbManager.connect())
        {
            userMap = dbManager.loadUsers();
            dbManager.disconnect();
        }
        
        boolean authenticated = false;
        
        //First check the user exists by accessing it from the user map
        User currentUser = userMap.get(username);
        if(currentUser != null)
        {
            //If the user exists on username call autheticate password to 
            //make sure the hashed passwords match
            if(authenticatePassword(password, currentUser.getHashPassword()))
            {
                authenticated = true;
                StageManager.setCurrentUser(currentUser);
            }
            else
            {
                StageManager.loadPopupMessage("Warning", "Password is incorrect for this user, "
                    + "please try again.", ButtonTypeEnum.OK);
            }
        }
        else
        {
            StageManager.loadPopupMessage("Warning", "A user with this username does not exist, "
                    + "please enter a valid username.", ButtonTypeEnum.OK);
        }
        return authenticated;
    }
    
    private boolean authenticatePassword(String passwordInput, String hashPassword)
    {
        boolean authenticated = false;
        try
        {
            //Uses the same method as the createPasswordHash but gets
            //the salt from the users hased password
            String[] splitPassword = hashPassword.split("\\$");
            byte[] salt = Base64.getDecoder().decode(splitPassword[1]);
            
            //Takes the input from the user and the salt from the stored hash
            //to recreate the hash.
            KeySpec spec = new PBEKeySpec(passwordInput.toCharArray(), salt, 65536, 128);
            SecretKeyFactory factory = SecretKeyFactory.getInstance(HASHALGORITHM);
            byte[] hash = factory.generateSecret(spec).getEncoded();
            
            //Encodes the byte[] so it can be matched against the stored hash
            String hashInput = Base64.getEncoder().encodeToString(hash);
            
            if(splitPassword[0].equals(hashInput))
                return true;
        }
        catch(NoSuchAlgorithmException | InvalidKeySpecException ex) 
        {
            System.out.println("Error when hashing password - " + ex.getMessage());
        }
        
        return authenticated;
    }
    
    public String createPasswordHash(String password)
    {
        String newPassword = "";
        try
        {
            //Creates a random sequence for each new hash to be
            //used when generating the hashed password, called a salt.
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[16];
            random.nextBytes(salt);
            
            //Using the password input and the random salt to create a 
            //128 key length hash. With 65536 acting as the strength param
            //or the amount of iterations it rehashes.
            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128);
            SecretKeyFactory factory = SecretKeyFactory.getInstance(HASHALGORITHM);
            
            byte[] hash = factory.generateSecret(spec).getEncoded();
            
            //Save the hash as the 'Hash' + '$' + 'Salt', as both are needed
            //for checking the password on login.
            newPassword = Base64.getEncoder().encodeToString(hash) + "$" + Base64.getEncoder().encodeToString(salt);
        }
        catch(NoSuchAlgorithmException | InvalidKeySpecException ex) 
        {
            System.out.println("Error when hashing password - " + ex.getMessage());
        }
        
        return newPassword;
    }
}
