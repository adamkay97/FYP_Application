package Classes;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

public class DatabaseManager 
{
    private static final String DBCONNSTRING = "jdbc:sqlite:Database\\FYP_Database.db";
    
    private static Connection conn;
    private static HashMap<String, User> userMap;
    
    public boolean connect() 
    {
        //Connects to SQLite database stored in the root of the application
        conn = null;
        try 
        {
            conn = DriverManager.getConnection(DBCONNSTRING);
            System.out.println("A connection to the SQLite db has been established.");    
            return true;
        } 
        catch (SQLException e) 
        {    
            System.out.println(e.getMessage());
            return false;
        }
    }
    
    public void loadQuestionList()
    {
        HashMap<Integer, Question> questionMap = new HashMap<>();
        
        String query = "SELECT * FROM QuestionList";
        
        //Loads all the questions in to the questionMap so it can be used 
        //in the questionaire form
        try(Statement stmt = conn.createStatement(); 
                ResultSet results = stmt.executeQuery(query)) 
        {    
            while(results.next())
            {
                int id = results.getInt("QuestionID");
                String text = results.getString("QuestionText");
                Question q = new Question(id, text);
                
                questionMap.put(id, q);
            }
            QuestionaireManager.setQuestionMap(questionMap);
        }
        catch(SQLException ex)
        {
             System.out.println("Error when reading questions from the db - " + ex.getMessage());
        }
    }
    
    public void loadUsers()
    {
        userMap = new HashMap<>();
        
        String query = "SELECT * FROM Users";
        
        try(Statement stmt = conn.createStatement(); 
                ResultSet results = stmt.executeQuery(query)) 
        {    
            while(results.next())
            {
                int id = results.getInt("UserID");
                String uname = results.getString("Username");
                String pword = results.getString("HashPassword");
                String fname = results.getString("FirstName");
                String lname = results.getString("LastName");
                User user = new User(id, uname, pword, fname, lname);
                
                userMap.put(uname, user);
            }
        }
        catch(SQLException ex)
        {
             System.out.println("Error when reading the user from the db - " + ex.getMessage());
        }
    }
    
    public static boolean writeUserToDatabase(User user)
    {
        boolean success = true;
        String query = "INSERT INTO Users VALUES (?, ?, ?, ?, ?)";
        
        try(PreparedStatement pstmt = conn.prepareStatement(query))
        {
            pstmt.setInt(1, user.getUserId());
            pstmt.setString(2, user.getUsername());
            pstmt.setString(3, user.getHashPassword());
            pstmt.setString(4, user.getFirstName());
            pstmt.setString(5, user.getLastName());
            pstmt.executeUpdate();
        }
        catch(SQLException ex)
        {
             System.out.println("Error when writing user to the db - " + ex.getMessage());
             success = false;
        }
        return success;
    }
    
    public String getResultInfo(int riskId)
    {
        String query = "SELECT * FROM ScoringRisk WHERE ScoringRiskID = ?";
        String resultText = "";
        
        try(PreparedStatement pstmt = conn.prepareStatement(query);) 
        {
            pstmt.setInt(1, riskId);
            ResultSet results = pstmt.executeQuery();
            
            while(results.next())
            {
                resultText = results.getString("RiskLevel") + "\n";
                resultText += results.getString("RiskText");
            }
        }
        catch(SQLException ex)
        {
             System.out.println("Error when reading the user from the db - " + ex.getMessage());
        }
        
        return resultText;
    }
    
    public static HashMap<String, User> getUserMap()
    {
        return userMap;
    }
    
    public void disconnect()
    {
        try 
        {
            if (conn != null)
            {
                conn.close();
                System.out.println("Successfully disconnected from the SQLite db.");
            }
        } 
        catch (SQLException ex) 
        {
            System.out.println(ex.getMessage());
        }
    }
}
