package structuralClasses;

import java.util.ArrayList;

public class CodecHeader {
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

