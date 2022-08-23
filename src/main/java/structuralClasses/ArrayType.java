public class ArrayType {
    String name = "";
    String elementType = "";
    int constantElementCount;

    public ArrayType(String name, String elementType, int constantElementCount) {
        this.name = name;
        this.elementType = elementType;
        this.constantElementCount = constantElementCount;
    }
    public ArrayType(String name, String elementType) {
        this.name = name;
        this.elementType = elementType;
        this.constantElementCount = -1;
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

    public int getConstantElementCount() {
        return constantElementCount;
    }

    public void setConstantElementCount(int constantElementCount) {
        this.constantElementCount = constantElementCount;
    }
}
