package Classes;

public class PopupText 
{
    private String header;
    private String message;
    
    public PopupText(String h, String m)
    {
        header = h;
        message = m;
    }
    
    public void setHeader(String h) { header = h; }
    public String getHeader() { return header; }
    
    public void setMessage(String m) { message = m; }
    public String getMessage() { return message; }
}
