package structuralClasses;

public class Primitives {
    String name = "";
    String format = "";
    int sizeInBytes;
    int sizeInBits;
    String type = "";

    public void setName(String name) {
        this.name = name;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public void setSizeInBytes(int sizeInBytes) {
        this.sizeInBytes = sizeInBytes;
    }

    public void setSizeInBits(int sizeInBits) {
        this.sizeInBits = sizeInBits;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getFormat() {
        return format;
    }

    public int getSizeInBytes() {
        return sizeInBytes;
    }

    public int getSizeInBits() {
        return sizeInBits;
    }

    public String getType() {
        return type;
    }

    public Primitives(String name, String format, int sizeInBytes, int sizeInBits, String type) {
        this.name = name;
        this.format = format;
        this.sizeInBytes = sizeInBytes;
        this.sizeInBits = sizeInBits;
        this.type = type;
    }

}
