import java.util.ArrayList;

public class Messages{
    String name = "";
    String id = "";
    ArrayList<MessageFields> Messagefields = new ArrayList<MessageFields>();

    public void setName(String name) {
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setMessagefields(ArrayList<MessageFields> messagefields) {
        Messagefields = messagefields;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public ArrayList<MessageFields> getMessageFields() {
        return Messagefields;
    }

    public Messages(String name, String id, ArrayList<MessageFields> Messagefields) {
        this.name = name;
        this.id = id;
        this.Messagefields = Messagefields;
    }

}

