package Classes;

public class FormText 
{
    private String form;
    private String elementId;
    private String textType;
    private String text;
    
    public FormText(String f, String id, String type, String t)
    {
        form = f;
        elementId = id;
        textType = type;
        text = t;
    }
    
    public void setForm(String f) { form = f; }
    public void setElementId(String id) { elementId = id; }
    public void setTextType(String type) { textType = type; }
    public void setText(String t) { text = t; }
    
    public String getForm() { return form; }
    public String getElementId() { return elementId; }
    public String getTextType() { return textType; }
    public String getText() { return text; }
}
