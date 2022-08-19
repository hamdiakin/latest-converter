public class Primitives {
    String name = "";
    String format = "";
    String sizeInBytes = "";
    String sizeInBits = "";
    String type = "";

    public void setName(String name) {
        this.name = name;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public void setSizeInBytes(String sizeInBytes) {
        this.sizeInBytes = sizeInBytes;
    }

    public void setSizeInBits(String sizeInBits) {
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

    public String getSizeInBytes() {
        return sizeInBytes;
    }

    public String getSizeInBits() {
        return sizeInBits;
    }

    public String getType() {
        return type;
    }

    public Primitives(String name, String format, String sizeInBytes, String sizeInBits, String type) {
        this.name = name;
        this.format = format;
        this.sizeInBytes = sizeInBytes;
        this.sizeInBits = sizeInBits;
        this.type = type;
    }

}
