package structuralClasses;

import java.util.ArrayList;


public class Structures {
    String name = "";
    ArrayList <StructureFields> Structurefields = new ArrayList <StructureFields>();


    public Structures(String name, ArrayList <StructureFields> Structurefields) {
        this.name = name;
        this.Structurefields = Structurefields;
    }

    public String getName() {
        return name;
    }

    public ArrayList<StructureFields> getStructurefields() {
        return Structurefields;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStructurefields(ArrayList<StructureFields> structurefields) {
        Structurefields = structurefields;
    }


}

