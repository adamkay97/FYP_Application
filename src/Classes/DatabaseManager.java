package Classes;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DatabaseManager 
{
    private final String DBCONNSTRING = "jdbc:sqlite:Database\\FYP_Database.db";
    
    private Connection conn;
    
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
    
    public HashMap<String, User> loadUsers()
    {
        HashMap<String, User> userMap = new HashMap<>();
        
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
            return userMap;
        }
        catch(SQLException ex)
        {
             System.out.println("Error when reading the users from the db - " + ex.getMessage());
        }
        return null;
    }
    
    public ArrayList<String> loadInformationData(int languageId, String pageName)
    {
        ArrayList<String> mchatInfo = new ArrayList<>();
        
        String query = "SELECT * FROM InformationPageData WHERE LanguageID = ? AND PageName = ?";
        
        try(PreparedStatement pstmt = conn.prepareStatement(query)) 
        {    
            pstmt.setInt(1, languageId);
            pstmt.setString(2, pageName);
            ResultSet results = pstmt.executeQuery();
            
            while(results.next())
            {
                String infoHeader = results.getString("InfoHeading");
                String infoText = results.getString("InfoText");
                mchatInfo.add(infoHeader + "%" + infoText);
            }
            return mchatInfo;
        }
        catch(SQLException ex)
        {
             System.out.println("Error when reading the MCHAT information from the db - " + ex.getMessage());
        }
        return null;
    }
    
    public boolean checkUserExists(String username)
    {
        boolean exists = false;
        String query = "SELECT * FROM Users WHERE Username = ?";
        
        try(PreparedStatement pstmt = conn.prepareStatement(query)) 
        {    
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            if(rs.first())
                exists = true;
        }
        catch(SQLException ex)
        {
            System.out.println("Error when checking the user exists in the db - " + ex.getMessage());
        }
        return exists;
    }
    
    public boolean checkChildExists(Child child)
    {
        boolean exists = false;
        String query = "SELECT * FROM Children WHERE Name = ? AND Age = ? AND Gender = ?";
        
        try(PreparedStatement pstmt = conn.prepareStatement(query)) 
        {    
            pstmt.setString(1, child.getChildName());
            pstmt.setInt(2, child.getChildAge());
            pstmt.setString(3, child.getChildGender());
            
            ResultSet rs = pstmt.executeQuery();
            
            if(rs.first())
                exists = true;
        }
        catch(SQLException ex)
        {
            System.out.println("Error when checking for an existing child in the db - " + ex.getMessage());
        }
        return exists;
    }
    
    public boolean writeUserToDatabase(User user)
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
    
    public boolean writeChildToDatabase(Child child)
    {
        boolean success = true;
        String query = "INSERT INTO Children VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try(PreparedStatement pstmt = conn.prepareStatement(query))
        {
            pstmt.setInt(1, child.getChildId());
            pstmt.setInt(2, child.getCurrentUserId());
            pstmt.setString(3, child.getChildName());
            pstmt.setInt(4, child.getChildAge());
            pstmt.setString(5, child.getChildGender());
            pstmt.setString(6, child.getResultText());
            pstmt.setInt(7, child.getResultScore());
            pstmt.executeUpdate();
        }
        catch(SQLException ex)
        {
             System.out.println("Error when writing child to the db - " + ex.getMessage());
             success = false;
        }
        return success;
    }
    
    public void updateChildScore(String scoreText, int score, int childId)
    {
        String query = "UPDATE Children SET ResultText = ?, ResultScore = ? WHERE ChildID = ?";
        
        try(PreparedStatement pstmt = conn.prepareStatement(query))
        {
            pstmt.setString(1, scoreText);
            pstmt.setInt(2, score);
            pstmt.setInt(3, childId);
            pstmt.executeUpdate();
        }
        catch(SQLException ex)
        {
            System.out.println("Error when updating childs score to the db - " + ex.getMessage());
        }
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
             System.out.println("Error when getting the result information from the db - " + ex.getMessage());
        }
        
        return resultText;
    }
    
    public int getNextUserID()
    {
        String query = "SELECT UserID FROM Users ORDER BY UserID DESC LIMIT 1";
        int userId = 0;
        
        try(Statement stmt = conn.createStatement(); 
                ResultSet results = stmt.executeQuery(query)) 
        {
            while(results.next())
                userId = results.getInt("UserId");
            
            userId++;
        }
        catch(SQLException ex)
        {
            System.out.println("Error when getting the next user ID from the db - " + ex.getMessage());
        }
        return userId;
    }
    
    public int getNextChildID()
    {
        String query = "SELECT ChildID FROM Children ORDER BY ChildID DESC LIMIT 1";
        int childId = 0;
        
        try(Statement stmt = conn.createStatement(); 
                ResultSet results = stmt.executeQuery(query)) 
        {
            while(results.next())
                childId = results.getInt("ChildID");
            
            childId++;
        }
        catch(SQLException ex)
        {
            System.out.println("Error when getting the next child Id from the db - " + ex.getMessage());
        }
        return childId;
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
