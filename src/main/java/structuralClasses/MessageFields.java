package structuralClasses;

public class MessageFields{
    String name = "";
    int fIndex ;
    String type = "";
    String elementCountField = "";
    
    public MessageFields(String name, int fIndex, String type, String elementCountField) {
        this.name = name;
        this.fIndex = fIndex;
        this.type = type;
        this.elementCountField = elementCountField;
    }

    public MessageFields(String name, int fIndex, String type) {
        this.name = name;
        this.fIndex = fIndex;
        this.type = type;
        this.elementCountField = "";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getfIndex() {
        return fIndex;
    }

    public void setfIndex(int fIndex) {
        this.fIndex = fIndex;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getElementCountField() {
        return elementCountField;
    }

    public void setElementCountField(String elementCountField) {
        this.elementCountField = elementCountField;
    }

   
    
}