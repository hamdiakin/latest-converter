package structuralClasses;

import java.util.ArrayList;

public class Messages{
    String name = "";
    int id ;
    ArrayList<MessageFields> Messagefields = new ArrayList<MessageFields>();

    public void setName(String name) {
        this.name = name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setMessagefields(ArrayList<MessageFields> messagefields) {
        Messagefields = messagefields;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public ArrayList<MessageFields> getMessageFields() {
        return Messagefields;
    }

    public Messages(String name, int id, ArrayList<MessageFields> Messagefields) {
        this.name = name;
        this.id = id;
        this.Messagefields = Messagefields;
    }

}

