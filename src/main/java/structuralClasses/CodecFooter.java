package structuralClasses;

import java.util.ArrayList;

public class CodecFooter {
    ArrayList<CodecFields> footerFields = new ArrayList<CodecFields>();

    public ArrayList<CodecFields> getFooterFields() {
        return footerFields;
    }

    public void setFooterFields(ArrayList<CodecFields> footerFields) {
        this.footerFields = footerFields;
    }

    public CodecFooter(ArrayList<CodecFields> footerFields) {
        this.footerFields = footerFields;
    }
}

