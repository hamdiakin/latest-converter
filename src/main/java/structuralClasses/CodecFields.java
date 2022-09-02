package structuralClasses;

public class CodecFields {
    private String name = "";
    private String fieldProperty = "";
    private int fieldIndex;
    private String type = "";
    private String defaultValue = "";

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

    public int getFieldIndex() {
        return fieldIndex;
    }

    public void setFieldIndex(int fieldIndex) {
        this.fieldIndex = fieldIndex;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public CodecFields(String name, String fieldProperty, int fieldIndex, String type) {
        this.name = name;
        this.fieldProperty = fieldProperty;
        this.fieldIndex = fieldIndex;
        this.type = type;
    }

    public CodecFields(String name, String fieldProperty, int fieldIndex, String type, String defaultValue) {
        this.name = name;
        this.fieldProperty = fieldProperty;
        this.fieldIndex = fieldIndex;
        this.type = type;
        this.defaultValue = defaultValue;
    }
}