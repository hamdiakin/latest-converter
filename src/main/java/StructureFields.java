class StructureFields {
    String name = "" ;
    String type = "";
    String elementCountStructureField = "";
    String fieldIndex = "";

    public String getFieldIndex() {
        return fieldIndex;
    }

    public void setFieldIndex(String fieldIndex) {
        this.fieldIndex = fieldIndex;
    }

    public StructureFields(String name, String type, String fieldIndex, String elementCountStructureField) {
        this.name = name;
        this.type = type;
        this.fieldIndex = fieldIndex;
        this.elementCountStructureField = elementCountStructureField;
    }

    public StructureFields(String name, String type, String fieldIndex) {
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