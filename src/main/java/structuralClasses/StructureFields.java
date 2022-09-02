package structuralClasses;

public class StructureFields {
    private String name = "" ;
    private String type = "";
    private String elementCountStructureField = "";
    private int fieldIndex;

    public int getFieldIndex() {
        return fieldIndex;
    }

    public void setFieldIndex(int fieldIndex) {
        this.fieldIndex = fieldIndex;
    }

    public StructureFields(String name, String type, int fieldIndex, String elementCountStructureField) {
        this.name = name;
        this.type = type;
        this.fieldIndex = fieldIndex;
        this.elementCountStructureField = elementCountStructureField;
    }

    public StructureFields(String name, String type, int fieldIndex) {
        this.name = name;
        this.type = type;
        this.fieldIndex = fieldIndex;
        this.elementCountStructureField = "";
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getElementCountStructureField() {
        return elementCountStructureField;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setElementCountStructureField(String elementCountStructureField) {
        this.elementCountStructureField = elementCountStructureField;
    }
}