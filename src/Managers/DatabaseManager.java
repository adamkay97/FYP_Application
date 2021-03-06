package Managers;

import Classes.Child;
import Classes.DiagnosisResult;
import Classes.FollowUpFlow;
import Classes.FollowUpPart;
import Classes.FormText;
import Classes.PopupText;
import Classes.Question;
import Classes.QuestionSet;
import Classes.ReviewData;
import Classes.ScoringAlgorithm;
import Classes.ScoringBound;
import Classes.User;
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
    //private final String DBCONNSTRING = "C:/Users/Adam/Documents/Degree/Third Year/Final Project/Application/FYP_Database.db";
    private Connection conn;
    private final LogManager logManager = new LogManager();
    
    public boolean connect() 
    {
        String DBCONNSTRING = SettingsManager.getDBConnString();
        
        //Connects to SQLite database stored in the root of the application
        conn = null;
        try 
        {
            conn = DriverManager.getConnection("jdbc:sqlite:" + DBCONNSTRING);  
            logManager.InfoLog("A connection to the SQLite db has been established.");
            
            return true;
        } 
        catch (SQLException e) 
        {
            logManager.ErrorLog("Failed when conencting to the Database - " + e.getMessage());
            return false;
        }
    }
    
    public void loadQuestionSetsMap()
    {
        ArrayList<String> questionSetNames = QuestionaireManager.getQuestionSetNames();
        HashMap<String, QuestionSet> questionSetsMap = new HashMap<>();
        
        for(String setName : questionSetNames)
        {
            ArrayList<String> languageList = getQuestionSetLanguages(setName);
            HashMap<Integer, Question> questionList = loadQuestionList(setName, languageList);
            ArrayList<String> information = loadInformationData(setName);
            ScoringAlgorithm algorithm = loadScoringAlgorithm(setName);
            
            questionSetsMap.put(setName, new QuestionSet(setName, questionList.size(), 
                                            questionList, languageList, information, algorithm));
        }
        QuestionaireManager.setQuestionSetsMap(questionSetsMap);
    }
    
    private HashMap<Integer, Question> loadQuestionList(String diagnosisName, ArrayList<String> languageList)
    {
        HashMap<Integer, Question> questionMap = new HashMap<>();
        
        String query = String.format("SELECT * FROM `%s`", diagnosisName);
        
        //Loads all the questions in to the questionMap so it can be used 
        //in the questionaire form for the specific diagnosis.
        try(Statement stmt = conn.createStatement(); 
                ResultSet results = stmt.executeQuery(query))
        {
            while(results.next())
            {
                HashMap<String, String> texts = new HashMap<>();
                HashMap<String, String> instructions = new HashMap<>();
                
                int qNumber = results.getInt("QuestionID");
                String qBehaviour = results.getString("BehaviourName");
                
                for(String language : languageList)
                {
                    String text = results.getString("QuestionText-"+language);
                    texts.put(language, text);
                    String instruction = results.getString("QuestionInstruction-"+language);
                    instructions.put(language, instruction);
                }
                
                String riskResponse = results.getString("AtRiskResponse");
                
                Question q = new Question(qNumber, qBehaviour, texts, instructions, riskResponse);
                questionMap.put(qNumber, q);
            }
            return questionMap;
        }
        catch(SQLException ex)
        {
            logManager.ErrorLog("Failed when reading questions from the db - " + ex.getMessage());
        }
        return null;
    }
    
    public ScoringAlgorithm loadScoringAlgorithm(String questionSet)
    {
        ScoringAlgorithm scoring = new ScoringAlgorithm();
        
        String query = "SELECT * FROM ScoringAlgorithm WHERE QuestionSetID = "
                     + "(SELECT QuestionSetID FROM QuestionSets WHERE SetName = ?)";
        
        try(PreparedStatement pstmt = conn.prepareStatement(query)) 
        {    
            pstmt.setString(1, questionSet);
            ResultSet results = pstmt.executeQuery();
            
            while(results.next())
            {
                int riskId = results.getInt("RiskLevelID");
                
                switch (riskId)
                {
                    case 1:
                        ScoringBound lowRisk = new ScoringBound
                            (results.getInt("LowerBound"), results.getInt("UpperBound"), riskId);
                        scoring.setLowRisk(lowRisk);
                        break;
                    case 2:
                        ScoringBound medRisk = new ScoringBound
                            (results.getInt("LowerBound"), results.getInt("UpperBound"), riskId);
                        scoring.setMedRisk(medRisk);
                        break;
                    case 3:
                        ScoringBound highRisk = new ScoringBound
                            (results.getInt("LowerBound"), results.getInt("UpperBound"), riskId);
                        scoring.setHighRisk(highRisk);
                        break;
                }
            }
        }
        catch(SQLException ex)
        {
            logManager.ErrorLog("Failed when reading the scoring algorithm from the db - " + ex.getMessage());
        }
        return scoring;
    }
    
    public void loadFollowUpList()
    {
        //Only load Follow Up questions if the current question set is for the MCHATR/F
        if (SettingsManager.getQuestionSet().equals("M-CHAT-R/F")) 
        {
            HashMap<Integer, FollowUpFlow> followUpList = new HashMap<>();
        
            for (int i = 1; i <= 20; i++) 
            {
                FollowUpFlow question = loadFollowUpQuestion(i);
                followUpList.put(i, question);
            }
            QuestionaireManager.setFollowUpMap(followUpList);
        }
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
                String text = results.getString("FlowText-"+LanguageManager.getLanguage());
                String type = results.getString("QuestionType");
                
                FollowUpPart followPart = new FollowUpPart(id, flowLevel, branch, text, type);
                partList.add(followPart);
            }
            
            followUp.createFollowUpFlow(partList, questionId);
            return followUp;
        }
        catch(SQLException ex)
        {
            logManager.ErrorLog("Failed when reading follow up questions from the db - " + ex.getMessage());
        }
        return null;
    }
    
    public void loadQuestionSetNames()
    {
        ArrayList<String> setNames = new ArrayList<>();
        
        String query = "SELECT * FROM QuestionSets";
        
        try(Statement stmt = conn.createStatement(); 
                ResultSet results = stmt.executeQuery(query)) 
        {    
            while(results.next())
            {
                String name = results.getString("SetName");
                setNames.add(name);
            }
            QuestionaireManager.setQuestionSetNames(setNames);
        }
        catch(SQLException ex)
        {
            logManager.ErrorLog("Failed when reading the Question Sets from the db - " + ex.getMessage());
        }
    }
    
    public void loadLanguageList()
    {
        ArrayList<String> languageList = new ArrayList<>();
        
        String query = "SELECT * FROM Languages";
        
        try(Statement stmt = conn.createStatement(); 
                ResultSet results = stmt.executeQuery(query)) 
        {    
            while(results.next())
            {
                String language = results.getString("Language");
                languageList.add(language);
            }
            LanguageManager.setLanguageList(languageList);
        }
        catch(SQLException ex)
        {
            logManager.ErrorLog("Failed when reading the Languages from the db - " + ex.getMessage());
        }
    }
    
    public User loadUser(int userId)
    {
        String query = "SELECT * FROM Users WHERE UserID = ?";
        
        try(PreparedStatement pstmt = conn.prepareStatement(query)) 
        {    
            pstmt.setInt(1, userId);
            ResultSet results = pstmt.executeQuery();
            
            while(results.next())
            {
                String uname = results.getString("Username");
                String pword = results.getString("HashPassword");
                String fname = results.getString("FirstName");
                String lname = results.getString("LastName");
                User user = new User(userId, uname, pword, fname, lname);
                
                return user;
            }
        }
        catch(SQLException ex)
        {
             logManager.ErrorLog("Failed when reading the user from the db - " + ex.getMessage());
        }
        return null;
    }
    
    public HashMap<String, ArrayList<FormText>> loadFormText(String language)
    {
        HashMap<String, ArrayList<FormText>> allFormText = new HashMap<>();
        ArrayList<String> forms = getFormNames();
        
        for(String formName : forms)
        {
            ArrayList<FormText> formText = new ArrayList<>();
            String query = "SELECT * FROM FormText WHERE Form = ?";
        
            try(PreparedStatement pstmt = conn.prepareStatement(query)) 
            {    
                pstmt.setString(1, formName);
                ResultSet results = pstmt.executeQuery();
                
                while(results.next())
                {
                    String form = results.getString("Form");
                    String elementId = results.getString("ElementID");
                    String type = results.getString("TextType");
                    String text = results.getString(language);
                    FormText fText = new FormText(form, elementId, type, text);

                    formText.add(fText);
                }
                allFormText.put(formName, formText);
            }
            catch(SQLException ex)
            {
                logManager.ErrorLog("Failed when reading the Form Text from the db - " + ex.getMessage());
                return null;
            }
        }
        return allFormText;
    }
    
    public HashMap<Integer, PopupText> loadPopupText(String language)
    {
        HashMap<Integer, PopupText> popupText = new HashMap<>();
        
        String query = "SELECT * FROM PopupText";
        
        try(Statement stmt = conn.createStatement(); 
                ResultSet results = stmt.executeQuery(query)) 
        {    
            while(results.next())
            {
                int id = results.getInt("PopupTextID");
                String header = results.getString("Header-"+language);
                String message = results.getString("Message-"+language);
                
                PopupText popup = new PopupText(header, message);
                popupText.put(id, popup);
            }
            return popupText;
        }
        catch(SQLException ex)
        {
            logManager.ErrorLog("Failed when reading the Popup Text from the db - " + ex.getMessage());
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
            logManager.ErrorLog("Failed when reading the users from the db - " + ex.getMessage());
        }
        return null;
    }
    
    public ArrayList<Child> loadChildren()
    {
        ArrayList<Child> childList = new ArrayList<>();
        
        String query = "SELECT c.ChildID, c.UserID, c.Name, c.Age, c.Gender, d.SetName, d.StageOneScore, "
                     + "d.StageOneRisk, d.StageTwoScore, d.OverallScreening FROM Children c "
                     + "JOIN DiagnosisResults d ON c.ChildID = d.ChildID "
                     + "WHERE d.StageOneScore IS NOT NULL AND c.UserID = ?";
        
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
                
                String setName = results.getString("SetName");
                int s1Score = results.getInt("StageOneScore");
                String s1Risk = results.getString("StageOneRisk");
                int s2Score = results.getInt("StageTwoScore");
                String screening = results.getString("OverallScreening");
                
                DiagnosisResult result = new DiagnosisResult(setName, s1Score, s1Risk, s2Score, screening);
                
                Child child = new Child(id, userId, name, age, gender, result);
                
                childList.add(child);
            }
            return childList;
        }
        catch(SQLException ex)
        {
            logManager.ErrorLog("Failed when reading the users from the db - " + ex.getMessage());
        }
        return null;
    }
    
    public ArrayList<String> loadInformationData(String pageName)
    {
        ArrayList<String> mchatInfo = new ArrayList<>();
        
        String query = "SELECT * FROM RichTextData WHERE PageName = ?";
        String language = LanguageManager.getLanguage();
        
        try(PreparedStatement pstmt = conn.prepareStatement(query)) 
        {    
            pstmt.setString(1, pageName);
            ResultSet results = pstmt.executeQuery();
            
            while(results.next())
            {
                String infoHeader = results.getString("InfoHeading-"+language);
                String infoText = results.getString("InfoText-"+language);
                
                if(infoText.equals(""))
                    infoText = " ";
                
                mchatInfo.add(infoHeader + "%" + infoText);
            }
            return mchatInfo;
        }
        catch(SQLException ex)
        {
            logManager.ErrorLog("Failed when reading the MCHAT information from the db - " + ex.getMessage());
        }
        return null;
    }
    
    public ArrayList<ReviewData> loadReviewData(int childId, int userId)
    {
        ArrayList<ReviewData> reviewDataList = new ArrayList<>();
        String language = LanguageManager.getLanguage();
        String qSet = SettingsManager.getQuestionSet();
        
        String query = "SELECT dr.QuestionID, ql.`QuestionText-"+ language +"`, dr.QuestionAnswer, "
                     + "dr.QuestionNotes, dr.FollowUpResult, dr.FollowUpAnswers "
                     + "FROM DiagnosisReviewData dr "
                     + "JOIN `" + qSet + "` ql ON dr.QuestionID = ql.QuestionID "
                     + "WHERE dr.ChildID = ? AND dr.UserID = ?";
        
        try(PreparedStatement pstmt = conn.prepareStatement(query)) 
        {    
            pstmt.setInt(1, childId);
            pstmt.setInt(2, userId);
            ResultSet results = pstmt.executeQuery();
            
            while(results.next())
            {
                int qId = results.getInt("QuestionID");
                String qText = results.getString("QuestionText-"+language);
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
            logManager.ErrorLog("Failed when reading the Review Data from the db - " + ex.getMessage());
        }
        return null;
    }
    
    public void loadApplicationSettings(int userId)
    {
        String query = "SELECT * FROM ApplicationSettings WHERE UserID = ?";
        
        try(PreparedStatement pstmt = conn.prepareStatement(query)) 
        {    
            pstmt.setInt(1, userId);
            ResultSet results = pstmt.executeQuery();
            
            while(results.next())
            {
                String usesNao = results.getString("UsesNaoRobot");
                String robotURL = results.getString("RobotConnectionURL");
                int robotVolume = results.getInt("RobotVolume");
                String noteMethod = results.getString("NoteMethod");
                String audioPath = results.getString("AudioFileLocation");
                String language = results.getString("FormLanguage");
                String questionSet = results.getString("QuestionSet"); 
                String setLanguage = results.getString("QuestionSetLanguage");
                
                SettingsManager.initialiseSettings(usesNao, robotURL, robotVolume, questionSet, 
                                                    setLanguage, language, noteMethod, audioPath);
            }
        }
        catch(SQLException ex)
        {
            logManager.ErrorLog("Failed when reading the Application settings from the db - " + ex.getMessage());
        }
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
            logManager.ErrorLog("Failed when checking the user exists in the db - " + ex.getMessage());
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
            logManager.ErrorLog("Failed when checking for an existing child in the db - " + ex.getMessage());
        }
        return exists;
    }
    
    public int checkForRememberedUser()
    {
        String query = "SELECT * FROM RememberedUser";
        int userId = -1;
        
        try(Statement stmt = conn.createStatement(); 
                ResultSet results = stmt.executeQuery(query)) 
        {    
            while(results.next())
                userId = results.getInt("UserID");
        }
        catch(SQLException ex)
        {
            logManager.ErrorLog("Failed when reading remembered users from the db - " + ex.getMessage());
        }
        return userId;
    }
    
    public boolean writeUserToDatabase(User user)
    {
        boolean success = true;
        String query = "INSERT INTO Users (Username, HashPassword, FirstName, LastName) "
                     + "VALUES (?, ?, ?, ?)";
        
        try(PreparedStatement pstmt = conn.prepareStatement(query))
        {
            //pstmt.setInt(1, user.getUserId());
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getHashPassword());
            pstmt.setString(3, user.getFirstName());
            pstmt.setString(4, user.getLastName());
            pstmt.executeUpdate();
        }
        catch(SQLException ex)
        {
            logManager.ErrorLog("Failed when writing user to the db - " + ex.getMessage());
            success = false;
        }
        return success;
    }
    
    public boolean writeChildToDatabase(Child child)
    {
        boolean success = true;
        String query = "INSERT INTO Children (UserID, Name, Age, Gender) "
                     + "VALUES (?, ?, ?, ?)";
        
        try(PreparedStatement pstmt = conn.prepareStatement(query))
        {
            pstmt.setInt(1, child.getCurrentUserId());
            pstmt.setString(2, child.getChildName());
            pstmt.setInt(3, child.getChildAge());
            pstmt.setString(4, child.getChildGender());
            pstmt.executeUpdate();
        }
        catch(SQLException ex)
        {
            logManager.ErrorLog("Failed when writing child to the db - " + ex.getMessage());
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
            logManager.ErrorLog("Failed when writing review data to the db - " + ex.getMessage());
        }
    }
    
    public void writeChildDiagnosisResult(int userId, int childId, String setName, int s1Score, String s1Risk, int s2Score, String result)
    {
        String query = "INSERT INTO DiagnosisResults (UserID, ChildID, SetName, StageOneScore, StageOneRisk, StageTwoScore, OverallScreening) "
                     + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try(PreparedStatement pstmt = conn.prepareStatement(query))
        {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, childId);
            pstmt.setString(3, setName);
            pstmt.setInt(4, s1Score);
            pstmt.setString(5, s1Risk);
            pstmt.setInt(6, s2Score);
            pstmt.setString(7, result);
            pstmt.executeUpdate();
        }
        catch(SQLException ex)
        {
            logManager.ErrorLog("Failed when writing childs diagnosis results to the db - " + ex.getMessage());
        }
    }
    
    public void createUserSettings(int userId)
    {
        String query = "INSERT INTO ApplicationSettings (UserID) VALUES (?)";
        
        try(PreparedStatement pstmt = conn.prepareStatement(query))
        {
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
        }
        catch(SQLException ex)
        {
            logManager.ErrorLog("Failed when creating user application settings in the db - " + ex.getMessage());
        }
    }
    
    public void updateUserSettings(int userId, String usesNao, String url, int volume, 
            String questionSet, String setLanguage, String language, String noteMethod, String audioPath)
    {
        String query = "UPDATE ApplicationSettings SET UsesNaoRobot = ?, RobotConnectionURL = ?, RobotVolume = ?, "
                     + "NoteMethod = ?, AudioFileLocation = ?, FormLanguage = ?, QuestionSet = ?, QuestionSetLanguage = ? "
                     + "WHERE UserID = ?";
        
        try(PreparedStatement pstmt = conn.prepareStatement(query))
        {
            pstmt.setString(1, usesNao);
            pstmt.setString(2, url);
            pstmt.setInt(3, volume);
            pstmt.setString(4, noteMethod);
            pstmt.setString(5, audioPath);
            pstmt.setString(6, language);
            pstmt.setString(7, questionSet);
            pstmt.setString(8, setLanguage);
            pstmt.setInt(9, userId);
            pstmt.executeUpdate();
        }
        catch(SQLException ex)
        {
            logManager.ErrorLog("Failed when updating application settings in the db - " + ex.getMessage());
        }
    }
    
    public DiagnosisResult getDiagnosisResults(int userId, int childId)
    {
        String query = "SELECT * FROM DiagnosisResults WHERE UserID = ? AND ChildID = ?";
        DiagnosisResult result = null;
        
        try(PreparedStatement pstmt = conn.prepareStatement(query)) 
        {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, childId);
            ResultSet results = pstmt.executeQuery();
            
            while(results.next())
            {
                String setName = results.getString("SetName");
                int s1Score = results.getInt("StageOneScore");
                String s1Risk = results.getString("StageOneRisk");
                int s2Score = results.getInt("StageTwoScore");
                String screening = results.getString("OverallScreening");
                
                result = new DiagnosisResult(setName, s1Score, s1Risk, s2Score, screening);
            }
        }
        catch(SQLException ex)
        {
            logManager.ErrorLog("Failed when getting the diagnosis result information from the db - " + ex.getMessage());
        }
        return result;
    }
    
    public ArrayList<String> getQuestionSetLanguages(String setName)
    {
         ArrayList<String> setLanguages = new ArrayList<>();
        
        String query = "SELECT s.SetName, l.Language FROM QuestionSetLanguages sl " +
                       "JOIN QuestionSets s on sl.QuestionSetID = s.QuestionSetID " +
                       "JOIN QuestionLanguages l on sl.QuestionLanguageID = l.QuestionLanguageID " +
                       "WHERE s.SetName = ?";
        
        try(PreparedStatement pstmt = conn.prepareStatement(query)) 
        {    
            pstmt.setString(1, setName);
            ResultSet results = pstmt.executeQuery();
            
            while(results.next())
            {
                String language = results.getString("Language");
                setLanguages.add(language);
            }
        }
        catch(SQLException ex)
        {
            logManager.ErrorLog("Failed when getting question set languages from the db - " + ex.getMessage());
        }
        return setLanguages;
    }
    
    public String getResultInfo(int riskId, int stage)
    {
        String query = "SELECT * FROM RiskLevel WHERE RiskLevelID = ?";
        String language = LanguageManager.getLanguage();
        String resultText = "";
        
        try(PreparedStatement pstmt = conn.prepareStatement(query)) 
        {
            pstmt.setInt(1, riskId);
            ResultSet results = pstmt.executeQuery();
            
            while(results.next())
            {
                if(stage == 1)
                    resultText = results.getString("RiskLevel-"+language) + "\n";
                
                resultText += results.getString("RiskText-"+language);
            }
        }
        catch(SQLException ex)
        {
             logManager.ErrorLog("Failed when getting the result information from the db - " + ex.getMessage());
        }
        
        return resultText;
    }
    
    public void rememberUser(int userId, boolean remember)
    {
        String query = "UPDATE RememberedUser SET UserID = ?";;
        
        try(PreparedStatement pstmt = conn.prepareStatement(query)) 
        {
            if(remember)
                pstmt.setInt(1, userId);
            else
                pstmt.setInt(1, -1);
            
            pstmt.executeUpdate();
        }
        catch(SQLException ex)
        {
            logManager.ErrorLog("Failed when setting Remembered User in the db - " + ex.getMessage());
        }
    }
    
    public int getQuestionSetID(String setName)
    {
        String query = "SELECT * FROM QuestionSets WHERE SetName = ?";
        int setId = 0;
        
        try(PreparedStatement pstmt = conn.prepareStatement(query)) 
        {
            pstmt.setString(1, setName);
            ResultSet results = pstmt.executeQuery();
            
            while(results.next())
                setId = results.getInt("QuestionSetID");
        }
        catch(SQLException ex)
        {
            logManager.ErrorLog("Failed when getting Question Set ID from the db - " + ex.getMessage());
        }
        return setId;
    }
    
    public String getNaoConnectionURL()
    {
        String query = "SELECT * FROM RobotData";
        String url = "";
        
        try(Statement stmt = conn.createStatement(); 
                ResultSet results = stmt.executeQuery(query)) 
        {    
            while(results.next())
                url = results.getString("RobotConnectionURL");
            
            return url;
        }
        catch(SQLException ex)
        {
            logManager.ErrorLog("Failed when reading the users from the db - " + ex.getMessage());
        }
        return url;
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
            logManager.ErrorLog("Failed when updating childs score to the db - " + ex.getMessage());
        }
    }
    
    public void decreaseSequenceID(String tableName)
    {
        int seq = getLastInsertedRowID(tableName);
        
        String query = "UPDATE sqlite_sequence SET seq = ? WHERE name = ?";
        
        try(PreparedStatement pstmt = conn.prepareStatement(query)) 
        {
            pstmt.setInt(1, seq-1);
            pstmt.setString(2, tableName);
            pstmt.executeUpdate();
        }
        catch(SQLException ex)
        {
            logManager.ErrorLog("Failed when getting the next child Id from the db - " + ex.getMessage());
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
            logManager.ErrorLog("Failed when getting the next child Id from the db - " + ex.getMessage());
        }
        return id;
    }
    
    private ArrayList<String> getFormNames()
    {
        String query = "SELECT DISTINCT Form FROM FormText";
        ArrayList<String> formNames = new ArrayList<>();
        
        try(Statement stmt = conn.createStatement(); 
                ResultSet results = stmt.executeQuery(query)) 
        {    
            while(results.next())
            {
                String formName = results.getString("Form");
                formNames.add(formName);
            }
            return formNames;
        }
        catch(SQLException ex)
        {
             logManager.ErrorLog("Failed when reading the Form Names in FormText from the db - " + ex.getMessage());
        }
        return null;
    }
    
    public void disconnect()
    {
        try 
        {
            if (conn != null)
            {
                conn.close();
                logManager.InfoLog("Successfully disconnected from the SQLite db.");
            }
        } 
        catch (SQLException ex) 
        {
            logManager.ErrorLog("Failed when disconnecting from Database - " + ex.getMessage());
        }
    }
}
