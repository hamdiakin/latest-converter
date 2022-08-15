import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Arrays;

public class formXML {
    private static final String FILE_NAME = "output.xlsx";

    // main method
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
            Sheet datatypeSheet = workbook.getSheetAt(4);
            Iterator<Row> iterator = datatypeSheet.iterator();

            // counter will be used for keeping track of codec via name of the main tags
            int counter;

            // variable for checking if the previous row is the same as the current row
            boolean contdPart = true;
            boolean samePart = true;
            String previousCodecName = "";
            String currentCodecName = "";

            // all the part below belongs to codec part
            // array list for holding a single codec info
            ArrayList<String[]> codecInfo = new ArrayList<String[]>();

            // array for holding line info in length of 10
            String[] lineInfo = new String[10];
            String[] lastItem = new String[10];

            while (iterator.hasNext()) {
                Row currentRow = iterator.next();
                Iterator<Cell> cellIterator = currentRow.iterator();
                counter = 0;
                while (cellIterator.hasNext()) {
                    Cell currentCell = cellIterator.next();
                    // System.out.print(currentCell.getStringCellValue() + "--");
                    counter++;
                    lineInfo[counter] = currentCell.getStringCellValue();
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

                if (contdPart == false) {
                    // object notations will be here
                    lastItem = codecInfo.get(codecInfo.size() - 1);
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
                        if (samePart) {
                            if (line[3].equals("HEADER")) {
                                headerFields.add(new CodecFields(line[4], line[5], line[6], line[7]));
                            } else if (line[3].equals("FOOTER")) {
                                footerFields.add(new CodecFields(line[4], line[5], line[6], line[7]));
                            } else if (line[3].equals("MESSAGES")) {
                                codecMessages.add(new CodecMessage(line[4]));
                            }
                        }
                        name = line[1];
                        byteOrder = line[2];
                    }
                    codec_list.add(new Codecs(name, byteOrder, new CodecHeader(headerFields),
                            new CodecFooter(footerFields), codecMessages));

                    contdPart = true;
                    // get the last item of the array list
                    headerFields.clear();
                    footerFields.clear();
                    codecMessages.clear();
                    codecInfo.clear();
                    codecInfo.add(lastItem);
                }

                System.out.println();
            }
            datatypeSheet = workbook.getSheetAt(0);
            iterator = datatypeSheet.iterator();

            // all the part below belongs to message type part
            contdPart = false;
            int sheetNumber = 3;
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

            // TODO: Can be redefined for every type
            name = "";
            String messageID = "";

            while (sheetNumber >= 0) {
                // dont touch above

                datatypeSheet = workbook.getSheetAt(sheetNumber);
                iterator = datatypeSheet.iterator();
                while (iterator.hasNext()) {
                    Row currentRow = iterator.next();
                    Iterator<Cell> cellIterator = currentRow.iterator();
                    counter = 0;
                    while (cellIterator.hasNext()) {
                        Cell currentCell = cellIterator.next();
                        System.out.print(currentCell.getStringCellValue() + "--");
                        counter++;
                        lineInfo[counter] = currentCell.getStringCellValue();
                    }
                    message_info.add(Arrays.copyOf(lineInfo, 10));

                    Arrays.fill(lineInfo, "");
                    System.out.println();

                    switch (sheetNumber) {
                        case 3:
                            if (message_info.size() > 1) {
                                previousIdentifier = message_info.get(message_info.size() - 2)[1];
                                currentIdentifier = message_info.get(message_info.size() - 1)[1];
                                if (previousIdentifier.equals(currentIdentifier)) {
                                    contdPart = true;
                                } else {
                                    contdPart = false;
                                }
                            }

                            if (contdPart == false) {
                                // object notations will be here
                                lastItem = message_info.get(message_info.size() - 1);
                                message_info.remove(message_info.size() - 1);
                                // iterate over the codecInfo array list and form the objects
                                for (String[] line : message_info) {
                                    if (line[5].equals("")) {
                                        message_fields.add(new MessageFields(line[3], line[4], line[5]));
                                    } else {
                                        message_fields.add(new MessageFields(line[3], line[4], line[5], line[6]));
                                    }
                                    name = line[1];
                                    messageID = line[2];
                                }

                                message_list.add(new Messages(name, messageID, message_fields));
                                contdPart = true;
                                // get the last item of the array list
                                message_info.clear();
                                message_info.add(lastItem);
                            }

                            break;
                        case 2:
                            arraytype_list.add(new ArrayType(message_info.get(0)[1], message_info.get(0)[2],
                                    message_info.get(0)[3]));
                            message_info.clear();
                            break;
                        case 1:
                            if (message_info.size() > 1) {
                                previousIdentifier = message_info.get(message_info.size() - 2)[1];
                                currentIdentifier = message_info.get(message_info.size() - 1)[1];
                                if (previousIdentifier.equals(currentIdentifier)) {
                                    contdPart = true;
                                } else {
                                    contdPart = false;
                                }
                            }

                            if (contdPart == false) {
                                // object notations will be here
                                lastItem = message_info.get(message_info.size() - 1);
                                message_info.remove(message_info.size() - 1);
                                // iterate over the codecInfo array list and form the objects
                                for (String[] line : message_info) {
                                    structure_fields.add(new StructureFields(line[2], line[3], line[4], line[5]));
                                    name = line[1];
                                }

                                structure_list.add(new Structures(name, structure_fields));
                                contdPart = true;
                                // get the last item of the array list
                                message_info.clear();
                                message_info.add(lastItem);
                            }

                            break;

                        case 0:
                            primitive_list.add(
                                    new Primitives(message_info.get(0)[1], message_info.get(0)[2],
                                            message_info.get(0)[3], message_info.get(0)[4], message_info.get(0)[5]));
                            message_info.clear();
                            break;

                    }
                }

                // dont touch below
                sheetNumber--;
            }
            workbook.close();
            System.out.println("Done");

            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // root elements
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("types");
            doc.appendChild(rootElement);

            for (Primitives primitive : primitive_list) {
                Element primitiveElement = doc.createElement("primitive");
                rootElement.appendChild(primitiveElement);
                primitiveElement.setAttribute("name", primitive.getName());
                primitiveElement.setAttribute("id", primitive.getFormat());
                primitiveElement.setAttribute("type", primitive.getSizeInBytes());
                primitiveElement.setAttribute("size", primitive.getSizeInBits());
                primitiveElement.setAttribute("byteOrder", primitive.getType());
            }

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

        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(output);

        transformer.transform(source, result);

    }
}