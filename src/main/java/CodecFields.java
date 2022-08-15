class CodecFields {
    String name = "" ;
    String fieldProperty = "";
    String fieldIndex = "";
    String type = "";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFieldProperty() {
        return fieldProperty;
    }

    public void setFieldProperty(String fieldProperty) {
        this.fieldProperty = fieldProperty;
    }

    public String getFieldIndex() {
        return fieldIndex;
    }

    public void setFieldIndex(String fieldIndex) {
        this.fieldIndex = fieldIndex;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public CodecFields(String name, String fieldProperty, String fieldIndex, String type) {
        this.name = name;
        this.fieldProperty = fieldProperty;
        this.fieldIndex = fieldIndex;
        this.type = type;
    }
}