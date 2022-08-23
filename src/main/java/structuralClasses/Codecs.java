package structuralClasses;

import java.util.ArrayList;

public class Codecs {

    String name = "";
    String byteOrder = "";
    CodecHeader codecHeader = null;
    CodecFooter codecFooter = null;
    ArrayList<CodecMessage> codecMessages = new ArrayList<CodecMessage>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getByteOrder() {
        return byteOrder;
    }

    public void setByteOrder(String byteOrder) {
        this.byteOrder = byteOrder;
    }

    public CodecHeader getCodecHeader() {
        return codecHeader;
    }

    public void setCodecHeader(CodecHeader codecHeader) {
        this.codecHeader = codecHeader;
    }

    public CodecFooter getCodecFooter() {
        return codecFooter;
    }

    public void setCodecFooter(CodecFooter codecFooter) {
        this.codecFooter = codecFooter;
    }

    public ArrayList<CodecMessage> getCodecMessages() {
        return codecMessages;
    }

    public void setCodecMessages(ArrayList<CodecMessage> codecMessages) {
        this.codecMessages = codecMessages;
    }

    public Codecs(String name, String byteOrder, CodecHeader codecHeader, CodecFooter codecFooter, ArrayList<CodecMessage> codecMessages) {
        this.name = name;
        this.byteOrder = byteOrder;
        this.codecHeader = codecHeader;
        this.codecFooter = codecFooter;
        this.codecMessages = codecMessages;
    }

}
