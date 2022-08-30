package xlsx2xml;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.OutputStream;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import enums.ExcelCellNumbers;

import java.util.Arrays;
import java.util.HashMap;
import structuralClasses.*;

public class XMLGenerator {
    private static final String FILE_NAME = "output.xlsx";

    public static void main(String[] args) throws ParserConfigurationException, TransformerException {
        try {

            // codec objects ist will be held here
            ArrayList<Codecs> codec_list = new ArrayList<Codecs>();
            ArrayList<CodecFields> headerFields = new ArrayList<CodecFields>();
            ArrayList<CodecFields> footerFields = new ArrayList<CodecFields>();
            ArrayList<CodecMessage> codecMessages = new ArrayList<CodecMessage>();
            String name = "";
            String byteOrder = "";

            // reading from an excel file
            FileInputStream excelFile = new FileInputStream(new File(FILE_NAME));
            Workbook workbook = new XSSFWorkbook(excelFile);
            Sheet datatypeSheet = workbook.getSheet("codecs");
            Iterator<Row> iterator = datatypeSheet.iterator();

            Sheet codecMessagesSheet = workbook.getSheet("codecMessages");

            HashMap<String, ArrayList<CodecMessage>> messageInfo4Codec = messageInfo4Codec(codecMessagesSheet);
            ArrayList<CodecMessage> codecMessage_empt = new ArrayList<CodecMessage>();
            codecMessage_empt.add(new CodecMessage("name"));

            // counter will be used for keeping track of codec via name of the main tags
            int counter;

            // variable for checking if the previous row is the same as the current row
            boolean contdPart = true;
            boolean samePart = true;
            String previousCodecName = "";
            String currentCodecName = "";
            boolean isEndOfCodecSheet = false;

            // all the part below belongs to codec part
            // array list for holding a single codec info
            ArrayList<String[]> codecInfo = new ArrayList<String[]>();

            // array for holding line info in length of 10
            String[] lineInfo = new String[10];
            String[] lastItem = new String[10];

            isEndOfCodecSheet = false;
            iterator.next();
            while (iterator.hasNext()) {
                Row currentRow = iterator.next();
                if (!iterator.hasNext())
                    isEndOfCodecSheet = true;
                Iterator<Cell> cellIterator = currentRow.iterator();
                counter = 0;
                while (cellIterator.hasNext()) {
                    Cell currentCell = cellIterator.next();
                    counter++;
                    if (currentCell.getCellType() == Cell.CELL_TYPE_STRING)
                        lineInfo[counter] = currentCell.getStringCellValue();
                    else
                        lineInfo[counter] = Integer.toString((int) currentCell.getNumericCellValue());
                }
                codecInfo.add(Arrays.copyOf(lineInfo, 10));

                // empty out the lineinfo array for the next row
                Arrays.fill(lineInfo, "");

                if (codecInfo.size() > 2) {
                    previousCodecName = codecInfo.get(codecInfo.size() - 2)[1];
                    currentCodecName = codecInfo.get(codecInfo.size() - 1)[1];
                    if (previousCodecName.equals(currentCodecName)) {
                        contdPart = true;
                    } else {
                        contdPart = false;
                    }
                }

                if (contdPart == false || isEndOfCodecSheet == true) {
                    // object notations will be here
                    lastItem = codecInfo.get(codecInfo.size() - 1);
                    if (contdPart == false && !isEndOfCodecSheet)
                        codecInfo.remove(codecInfo.size() - 1);

                    // iterate over the codecInfo array list and form the objects
                    String lineThree = codecInfo.get(0)[3];
                    for (String[] line : codecInfo) {
                        if (line[3].equals(lineThree)) {
                            samePart = true;
                        } else {
                            lineThree = line[3];
                            samePart = false;
                        }

                        if (line[ExcelCellNumbers.C3.getValue()].equals("HEADER")) {
                            headerFields.add(
                                    new CodecFields(line[ExcelCellNumbers.C4.getValue()],
                                            line[ExcelCellNumbers.C5.getValue()],
                                            Integer.parseInt(line[ExcelCellNumbers.C6.getValue()]),
                                            line[ExcelCellNumbers.C7.getValue()],
                                            line[ExcelCellNumbers.C8.getValue()]));
                        } else if (line[ExcelCellNumbers.C3.getValue()].equals("FOOTER")) {
                            footerFields.add(
                                    new CodecFields(line[ExcelCellNumbers.C4.getValue()],
                                            line[ExcelCellNumbers.C5.getValue()],
                                            Integer.parseInt(line[ExcelCellNumbers.C6.getValue()]),
                                            line[ExcelCellNumbers.C7.getValue()],
                                            line[ExcelCellNumbers.C8.getValue()]));

                        } else if (line[ExcelCellNumbers.C3.getValue()].equals("MESSAGES")) {
                            codecMessages.add(new CodecMessage(line[4]));
                        }

                        name = line[1];
                        byteOrder = line[2];
                    }
                    codec_list.add(new Codecs(name, byteOrder, new CodecHeader(new ArrayList<>(headerFields)),
                            new CodecFooter(new ArrayList<>(footerFields)),
                            new ArrayList<>(messageInfo4Codec.get(name))));

                    contdPart = true;
                    // get the last item of the array list
                    headerFields.clear();
                    footerFields.clear();
                    codecMessages.clear();
                    codecInfo.clear();
                    codecInfo.add(lastItem);
                }

                // System.out.println();
            }
            datatypeSheet = workbook.getSheetAt(0);
            iterator = datatypeSheet.iterator();

            // all the part below belongs to message type part
            contdPart = true;
            counter = 0;
            String previousIdentifier = "";
            String currentIdentifier = "";
            ArrayList<String[]> message_info = new ArrayList<String[]>();
            ArrayList<Messages> message_list = new ArrayList<Messages>();
            ArrayList<MessageFields> message_fields = new ArrayList<MessageFields>();

            ArrayList<ArrayType> arraytype_list = new ArrayList<ArrayType>();

            ArrayList<Structures> structure_list = new ArrayList<Structures>();
            ArrayList<StructureFields> structure_fields = new ArrayList<StructureFields>();
            ArrayList<Primitives> primitive_list = new ArrayList<Primitives>();

            name = "";
            int messageID = -1;

            int wbSheetNumber = workbook.getNumberOfSheets();
            String sheetNames[] = new String[wbSheetNumber];
            boolean isEndOfSheet = false;

            // get sheet names and append them into a string array
            for (int i = 0; i < wbSheetNumber; i++) {
                sheetNames[i] = workbook.getSheetName(i);
            }

            for (String sheetName : sheetNames) {
                datatypeSheet = workbook.getSheet(sheetName);
                message_info.clear();
                isEndOfSheet = false;
                iterator = datatypeSheet.iterator();
                iterator.next();

                while (iterator.hasNext()) {
                    // checking only the sheet name is making an edge case
                    // when it's end of the sheet and contd true the content of message info will be
                    // carried out to the next sheet causing problems

                    Row currentRow = iterator.next();

                    if (iterator.hasNext() == false)
                        isEndOfSheet = true;

                    Iterator<Cell> cellIterator = currentRow.iterator();
                    counter = 0;
                    while (cellIterator.hasNext()) {
                        Cell currentCell = cellIterator.next();
                        // System.out.print(currentCell.getStringCellValue() + "--");
                        counter++;
                        if (currentCell.getCellType() == Cell.CELL_TYPE_STRING)
                            lineInfo[counter] = currentCell.getStringCellValue();
                        else
                            lineInfo[counter] = Integer.toString((int) currentCell.getNumericCellValue());
                    }
                    message_info.add(Arrays.copyOf(lineInfo, 10));

                    Arrays.fill(lineInfo, "");
                    // System.out.println();

                    switch (sheetName) {
                        case "messages":
                            if (message_info.size() > 1) {
                                previousIdentifier = message_info.get(message_info.size() - 2)[1];
                                currentIdentifier = message_info.get(message_info.size() - 1)[1];
                                if (previousIdentifier.equals(currentIdentifier)) {
                                    contdPart = true;
                                } else {
                                    contdPart = false;
                                }
                            }
                            if (contdPart == false || isEndOfSheet == true) {

                                lastItem = message_info.get(message_info.size() - 1);
                                message_info.remove(message_info.size() - 1);

                                // iterate over the codecInfo array list and form the objects
                                for (String[] line : message_info) {
                                    if (line[5].equals("")) {
                                        message_fields
                                                .add(new MessageFields(line[3], Integer.parseInt(line[4]), line[5]));
                                    } else {
                                        message_fields.add(new MessageFields(line[3], Integer.parseInt(line[4]),
                                                line[5], line[6]));
                                    }
                                    name = line[1];
                                    messageID = Integer.parseInt(line[2]);
                                }

                                message_list.add(new Messages(name, messageID, new ArrayList<>(message_fields)));
                                contdPart = true;
                                // get the last item of the array list
                                message_info.clear();
                                message_fields.clear();
                                message_info.add(lastItem);
                            }
                            break;
                        case "arrays":
                            if (message_info.get(0)[3].equals(""))

                                arraytype_list.add(new ArrayType(message_info.get(0)[1], message_info.get(0)[2]));
                            else
                                arraytype_list.add(new ArrayType(message_info.get(0)[1], message_info.get(0)[2],
                                        Integer.parseInt(message_info.get(0)[3])));

                            message_info.clear();
                            break;
                        case "structures":
                            if (message_info.size() > 1) {
                                previousIdentifier = message_info.get(message_info.size() - 2)[1];
                                currentIdentifier = message_info.get(message_info.size() - 1)[1];
                                if (previousIdentifier.equals(currentIdentifier)) {
                                    contdPart = true;
                                } else {
                                    contdPart = false;
                                }
                            }

                            if (contdPart == false || isEndOfSheet == true) {
                                // object notations will be here
                                lastItem = message_info.get(message_info.size() - 1);
                                if (contdPart == false && !isEndOfSheet)
                                    message_info.remove(message_info.size() - 1);
                                // iterate over the codecInfo array list and form the objects
                                for (String[] line : message_info) {
                                    structure_fields.add(
                                            new StructureFields(line[2], line[3], Integer.parseInt(line[4]), line[5]));
                                    name = line[1];
                                }

                                structure_list.add(new Structures(name, structure_fields));
                                contdPart = true;
                                // get the last item of the array list
                                message_info.clear();
                                message_info.add(lastItem);
                            }

                            break;

                        case "primitives":
                            primitive_list.add(
                                    new Primitives(message_info.get(0)[1], message_info.get(0)[2],
                                            Integer.parseInt(message_info.get(0)[4]),
                                            Integer.parseInt(message_info.get(0)[5]), message_info.get(0)[3]));
                            message_info.clear();
                            break;
                    }

                }

            }

            workbook.close();
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // root elements
            Document doc = docBuilder.newDocument();
            Element root = doc.createElement("TA");
            doc.appendChild(root);

            putLineBreak(doc, root);

            root.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
            root.setAttribute("xsi:noNamespaceSchemaLocation", "TA.xsd");

            Element rootTypes = doc.createElement("types");
            root.appendChild(rootTypes);

            for (Primitives primitive : primitive_list) {
                Element primitiveElement = doc.createElement("primitive");
                rootTypes.appendChild(primitiveElement);
                primitiveElement.setAttribute("name", primitive.getName());
                primitiveElement.setAttribute("format", primitive.getFormat());
                primitiveElement.setAttribute("sizeInBytes", Integer.toString(primitive.getSizeInBytes()));
                primitiveElement.setAttribute("sizeInBits", Integer.toString(primitive.getSizeInBits()));
                primitiveElement.setAttribute("type", primitive.getType());
            }
            putLineBreak(doc, rootTypes);

            for (Structures structure : structure_list) {
                Element structureElement = doc.createElement("structure");
                rootTypes.appendChild(structureElement);
                structureElement.setAttribute("name", structure.getName());
                for (StructureFields field : structure.getStructurefields()) {
                    Element fieldElement = doc.createElement("field");
                    structureElement.appendChild(fieldElement);
                    fieldElement.setAttribute("name", field.getName());
                    fieldElement.setAttribute("type", field.getType());
                    if (!field.getElementCountStructureField().equals(""))
                        fieldElement.setAttribute("elementCountStructureField",
                                field.getElementCountStructureField());
                    fieldElement.setAttribute("fieldIndex", Integer.toString(field.getFieldIndex()));
                }
            }

            putLineBreak(doc, rootTypes);

            for (ArrayType arrayType : arraytype_list) {
                Element arrayTypeElement = doc.createElement("arrayType");
                rootTypes.appendChild(arrayTypeElement);
                arrayTypeElement.setAttribute("name", arrayType.getName());
                arrayTypeElement.setAttribute("elementType", arrayType.getElementType());
                if (arrayType.getConstantElementCount() != -1) {
                    arrayTypeElement.setAttribute("constantElementCount",
                            Integer.toString(arrayType.getConstantElementCount()));
                }

            }

            putLineBreak(doc, root);

            Element messagesElement = doc.createElement("messages");
            root.appendChild(messagesElement);

            for (Messages message : message_list) {
                Element messageElement = doc.createElement("message");
                messagesElement.appendChild(messageElement);
                messageElement.setAttribute("name", message.getName());
                messageElement.setAttribute("id", Integer.toString(message.getId()));
                for (MessageFields field : message.getMessageFields()) {
                    Element fieldElement = doc.createElement("field");
                    messageElement.appendChild(fieldElement);
                    fieldElement.setAttribute("name", field.getName());
                    fieldElement.setAttribute("type", field.getType());
                    if (!field.getElementCountField().equals(""))
                        fieldElement.setAttribute("elementCountMessageField",
                                field.getElementCountField());
                    fieldElement.setAttribute("fieldIndex", Integer.toString(field.getfIndex()));
                }
                Text lineBreakMessages = doc.createTextNode("\n");
                messagesElement.appendChild(lineBreakMessages);
            }

            Text lineBreak3 = (Text) doc.createTextNode("\n\n");
            root.appendChild(lineBreak3);

            Element rootCodecs = doc.createElement("codecs");
            root.appendChild(rootCodecs);
            putLineBreak(doc, root);

            for (Codecs codec : codec_list) {
                Element codecElement = doc.createElement("codec");
                rootCodecs.appendChild(codecElement);
                codecElement.setAttribute("name", codec.getName());
                codecElement.setAttribute("byteOrder", codec.getByteOrder());

                Element headerElement = doc.createElement("header");
                codecElement.appendChild(headerElement);
                for (CodecFields field : codec.getCodecHeader().getHeaderFields()) {

                    Element fieldElement = doc.createElement("field");
                    headerElement.appendChild(fieldElement);
                    fieldElement.setAttribute("name", field.getName());
                    if (!field.getFieldProperty().equals(""))
                        fieldElement.setAttribute("fieldProperty", field.getFieldProperty());
                    fieldElement.setAttribute("fieldIndex", Integer.toString(field.getFieldIndex()));
                    fieldElement.setAttribute("type", field.getType());
                    if (!field.getDefaultValue().equals(""))
                        fieldElement.setAttribute("defaultValue", field.getDefaultValue());
                }

                Element footerElement = doc.createElement("footer");
                codecElement.appendChild(footerElement);

                for (CodecFields field : codec.getCodecFooter().getFooterFields()) {
                    Element fieldElement = doc.createElement("field");

                    footerElement.appendChild(fieldElement);
                    fieldElement.setAttribute("name", field.getName());
                    fieldElement.setAttribute("fieldProperty", field.getFieldProperty());
                    fieldElement.setAttribute("fieldIndex", Integer.toString(field.getFieldIndex()));
                    fieldElement.setAttribute("type", field.getType());
                }

                Text codecAfterFooter = doc.createTextNode("\n");
                codecElement.appendChild(codecAfterFooter);

                for (CodecMessage codecMessage : codec.getCodecMessages()) {
                    Element messageElement = doc.createElement("message");
                    codecElement.appendChild(messageElement);
                    messageElement.setAttribute("name", codecMessage.getName());

                }
                Text codecAfterMessages = doc.createTextNode("\n");
                codecElement.appendChild(codecAfterMessages);

                if (!(codec == codec_list.get(codec_list.size() - 1))) {
                    Text lineBreakCodecs = doc.createTextNode("\n");
                    rootCodecs.appendChild(lineBreakCodecs);
                }
            }

            putLineBreak(doc, root);

            // print XML to system console
            writeXml(doc, System.out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // write doc to output stream
    private static void writeXml(Document doc,
            OutputStream output)
            throws TransformerException {

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();

        // pretty print
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
        transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, "");

        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File("outputXML.xml"));

        transformer.transform(source, result);

    }

    public static void putLineBreak(Document doc, Element element) {
        Text linebreak = (Text) doc.createTextNode("\n");
        element.appendChild(linebreak);
    }

    public static HashMap<String, ArrayList<CodecMessage>> messageInfo4Codec(Sheet sheet) {
        ArrayList<CodecMessage> codecMessageList = new ArrayList<CodecMessage>();
        Iterator<Row> rowIterator = sheet.iterator();

        boolean isEndOfSheet = false;
        HashMap<String, ArrayList<CodecMessage>> codecMessageMap = new HashMap<String, ArrayList<CodecMessage>>();
        int counter = 0;
        String[] lineInfo = new String[10];
        ArrayList<String[]> codecInfo = new ArrayList<String[]>();
        String previousCodecName = "";
        String currentCodecName = "";
        boolean contdPart = true;
        String[] lastItem = new String[10];
        String codecName = "";
        rowIterator.next();

        while (rowIterator.hasNext()) {
            Row currentRow = rowIterator.next();
            if (!rowIterator.hasNext())
                isEndOfSheet = true;

            Iterator<Cell> cellIterator = currentRow.iterator();
            counter = 0;

            while (cellIterator.hasNext()) {
                Cell currentCell = cellIterator.next();
                counter++;
                lineInfo[counter] = currentCell.getStringCellValue();
            }
            codecInfo.add(Arrays.copyOf(lineInfo, 10));

            // empty out the lineinfo array for the next row
            Arrays.fill(lineInfo, "");

            if (codecInfo.size() > 1) {
                previousCodecName = codecInfo.get(codecInfo.size() - 2)[1];
                currentCodecName = codecInfo.get(codecInfo.size() - 1)[1];
                if (previousCodecName.equals(currentCodecName)) {
                    contdPart = true;
                } else {
                    contdPart = false;
                }
            }
            if (contdPart == false || isEndOfSheet == true) {
                lastItem = codecInfo.get(codecInfo.size() - 1);
                codecInfo.remove(codecInfo.size() - 1);
                for (String[] item : codecInfo) {
                    codecMessageList.add(new CodecMessage(item[4]));
                }

                contdPart = true;
                codecName = codecInfo.get(0)[1];
                codecMessageMap.put(codecName, new ArrayList<CodecMessage>(codecMessageList));
                codecInfo.clear();
                codecMessageList.clear();
                codecInfo.add(lastItem);
                if (isEndOfSheet && contdPart == true) {
                    codecName = codecInfo.get(0)[1];
                    for (String[] item : codecInfo) {
                        codecMessageList.add(new CodecMessage(item[4]));

                    }
                    codecMessageMap.put(codecName, new ArrayList<CodecMessage>(codecMessageList));
                }

            }
        }

        return codecMessageMap;
    }
}
