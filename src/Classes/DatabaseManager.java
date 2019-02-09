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
    
    public void loadFollowUpList()
    {
        HashMap<Integer, FollowUpFlow> followUpList = new HashMap<>();
        
        for (int i = 1; i <= 20; i++) 
        {
            FollowUpFlow question = loadFollowUpQuestion(i);
            followUpList.put(i, question);
        }
        QuestionaireManager.setFollowUpMap(followUpList);
    }
    
    private FollowUpFlow loadFollowUpQuestion(int questionId)
    {
        FollowUpFlow followUp = new FollowUpFlow();
        String query = "SELECT * FROM FollowUpQuestionList WHERE QuestionID = ?";
        List<FollowUpPart> partList = new ArrayList<>();
        
        //Loads all the questions in to the questionMap so it can be used 
        //in the questionaire form
        try(PreparedStatement pstmt = conn.prepareStatement(query)) 
        {    
            pstmt.setInt(1, questionId);
            ResultSet results = pstmt.executeQuery();
            
            while(results.next())
            {
                int id = results.getInt("QuestionID");
                int flowLevel = results.getInt("FlowLevel");
                String branch = results.getString("FlowBranch");
                String text = results.getString("FlowText");
                String type = results.getString("QuestionType");
                
                FollowUpPart followPart = new FollowUpPart(id, flowLevel, branch, text, type);
                partList.add(followPart);
            }
            
            followUp.createFollowUpFlow(partList, questionId);
            return followUp;
        }
        catch(SQLException ex)
        {
            System.out.println("Error when reading follow up questions from the db - " + ex.getMessage());
        }
        return null;
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
    
    public ArrayList<Child> loadChildren()
    {
        ArrayList<Child> childList = new ArrayList<>();
        
        String query = "SELECT * FROM Children WHERE ResultText IS NOT NULL AND UserID = ?";
        
        try(PreparedStatement pstmt = conn.prepareStatement(query)) 
        {    
            pstmt.setInt(1, StageManager.getCurrentUser().getUserId());
            ResultSet results = pstmt.executeQuery();
            
            while(results.next())
            {
                int id = results.getInt("ChildID");
                int userId = results.getInt("UserID");
                String name = results.getString("Name");
                int age = results.getInt("Age");
                String gender = results.getString("Gender");
                String resultText = results.getString("ResultText");
                int resultScore = results.getInt("ResultScore");
                Child child = new Child(id, userId, name, age, gender, resultText, resultScore);
                
                childList.add(child);
            }
            return childList;
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
        
        String query = "SELECT * FROM RichTextData WHERE LanguageID = ? AND PageName = ?";
        
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
    
    public ArrayList<ReviewData> loadReviewData(int childId, int userId)
    {
        ArrayList<ReviewData> reviewDataList = new ArrayList<>();
        
        String query = "SELECT dr.QuestionID, ql.QuestionText, dr.QuestionAnswer, dr.QuestionNotes, dr.FollowUpResult, dr.FollowUpAnswers "
                    + "FROM DiagnosisReviewData dr "
                    + "JOIN QuestionList ql ON dr.QuestionID = ql.QuestionID "
                    + "WHERE dr.ChildID = ? AND dr.UserID = ?";
        
        try(PreparedStatement pstmt = conn.prepareStatement(query)) 
        {    
            pstmt.setInt(1, childId);
            pstmt.setInt(2, userId);
            ResultSet results = pstmt.executeQuery();
            
            while(results.next())
            {
                int qId = results.getInt("QuestionID");
                String qText = results.getString("QuestionText");
                String qAnswer = results.getString("QuestionAnswer");
                String qNotes = results.getString("QuestionNotes");
                String fResult = results.getString("FollowUpResult");
                String fAnswer = results.getString("FollowUpAnswers");
                
                ReviewData reviewData = new ReviewData(qId, qText, qAnswer, qNotes, fResult, fAnswer);
                reviewDataList.add(reviewData);
            }
            return reviewDataList;
        }
        catch(SQLException ex)
        {
             System.out.println("Error when reading the Review Data from the db - " + ex.getMessage());
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
            
            if(rs.next())
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
            
            if(rs.next())
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
        String query = "INSERT INTO Children (UserID, Name, Age, Gender, ResultText, ResultScore) "
                     + "VALUES (?, ?, ?, ?, ?, ?)";
        
        try(PreparedStatement pstmt = conn.prepareStatement(query))
        {
            pstmt.setInt(1, child.getCurrentUserId());
            pstmt.setString(2, child.getChildName());
            pstmt.setInt(3, child.getChildAge());
            pstmt.setString(4, child.getChildGender());
            pstmt.setString(5, child.getResultText());
            pstmt.setInt(6, child.getResultScore());
            pstmt.executeUpdate();
        }
        catch(SQLException ex)
        {
             System.out.println("Error when writing child to the db - " + ex.getMessage());
             success = false;
        }
        
        //If it doenst fail set the childs id to the auto incremented value
        if(success)
            child.setChildId(getLastInsertedRowID("Children"));
        
        return success;
    }
    
    public void writeDiagnosisToDatabase(int uId, int cId, int qId, String qAnswer, 
            String qNotes, String fResult, String fText)
    {
        String query = "INSERT INTO DiagnosisReviewData (UserID, ChildID, QuestionID, QuestionAnswer, QuestionNotes, "
                       + "FollowUpResult, FollowUpAnswers) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try(PreparedStatement pstmt = conn.prepareStatement(query))
        {
            pstmt.setInt(1, uId);
            pstmt.setInt(2, cId);
            pstmt.setInt(3, qId);
            pstmt.setString(4, qAnswer);
            pstmt.setString(5, qNotes);
            pstmt.setString(6, fResult);
            pstmt.setString(7, fText);
            pstmt.executeUpdate();
        }
        catch(SQLException ex)
        {
             System.out.println("Error when writing review data to the db - " + ex.getMessage());
        }
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
    
    public void removeCurrentChild(int childId)
    {
        String query = "DELETE FROM Children WHERE ChildID = ?";
        
        try(PreparedStatement pstmt = conn.prepareStatement(query))
        {
            pstmt.setInt(1, childId);
            pstmt.executeUpdate();
        }
        catch(SQLException ex)
        {
            System.out.println("Error when updating childs score to the db - " + ex.getMessage());
        }
    }
    
    public int getLastInsertedRowID(String tableName)
    {
        String query = "SELECT seq FROM sqlite_sequence WHERE name = ?";
        int id = 0;
        
        try(PreparedStatement pstmt = conn.prepareStatement(query)) 
        {
            pstmt.setString(1, tableName);
            ResultSet results = pstmt.executeQuery();
            
            while(results.next())
                id = results.getInt("seq");
        }
        catch(SQLException ex)
        {
            System.out.println("Error when getting the next child Id from the db - " + ex.getMessage());
        }
        return id;
    }
    
    public String getResultInfo(int riskId, int stage)
    {
        String query = "SELECT * FROM ScoringRisk WHERE ScoringRiskID = ?";
        String resultText = "";
        
        try(PreparedStatement pstmt = conn.prepareStatement(query)) 
        {
            pstmt.setInt(1, riskId);
            ResultSet results = pstmt.executeQuery();
            
            while(results.next())
            {
                if(stage == 1)
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
