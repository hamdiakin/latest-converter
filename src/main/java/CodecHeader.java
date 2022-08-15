import java.util.ArrayList;

class CodecHeader {
    ArrayList<CodecFields> headerFields = new ArrayList<CodecFields>();

    public ArrayList<CodecFields> getHeaderFields() {
        return headerFields;
    }

    public void setHeaderFields(ArrayList<CodecFields> headerFields) {
        this.headerFields = headerFields;
    }

    public CodecHeader(ArrayList<CodecFields> headerFields) {
        this.headerFields = headerFields;
    }
}

