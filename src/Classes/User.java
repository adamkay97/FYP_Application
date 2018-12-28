package Classes;

public class User 
{
    private final int userID;
    private final String username;
    private final String hashPassword;
    private final String firstName;
    private final String lastName;
    
    public User(int id, String uname, String pword, 
            String fName, String lName) 
    {
        userID = id;
        username = uname;
        hashPassword = pword;
        firstName = fName;
        lastName = lName;
    }
        
    public int getUserId()
    {
        return userID;
    }
    
    public String getUsername()
    {
        return username;
    }
    
    public String getHashPassword()
    {
        return hashPassword;
    }
    
    public String getFirstName()
    {
        return firstName;
    }
    
    public String getLastName()
    {
        return lastName;
    }
    
    
}
