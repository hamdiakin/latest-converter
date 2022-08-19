public class ArrayType {
    String name = "";
    String elementType = "";
    String constantElementCount = "";

    public ArrayType(String name, String elementType, String constantElementCount) {
        this.name = name;
        this.elementType = elementType;
        this.constantElementCount = constantElementCount;
    }
    public ArrayType(String name, String elementType) {
        this.name = name;
        this.elementType = elementType;
        this.constantElementCount = "";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getElementType() {
        return elementType;
    }

    public void setElementType(String elementType) {
        this.elementType = elementType;
    }

    public String getConstantElementCount() {
        return constantElementCount;
    }

    public void setConstantElementCount(String constantElementCount) {
        this.constantElementCount = constantElementCount;
    }
}
