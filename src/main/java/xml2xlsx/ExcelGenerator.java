package xml2xlsx;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import enums.ExcelCellNumbers;
import enums.XsdElementLevels;
import structuralClasses.*;

import org.w3c.dom.Element;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.*;

public class ExcelGenerator {
    private static final String FILE_NAME = "./src/main/outputs/output.xlsx";
    private static final String XSD_SOURCE_PATH = "./src/main/resources/TA.xsd";
    private static final String INPUT_PATH = "./src/main/resources/easyMessageTest.xml";

    // for the thread pool
    ExecutorService executor = Executors.newFixedThreadPool(10);
    // main method
    public static void main(String[] args) {

        File xmlFile = new File(INPUT_PATH);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        // Validations
        Set<String> codecFieldType = new LinkedHashSet<>();
        Set<String> messageNames = new LinkedHashSet<>();
        Set<String> arrayTypes = new LinkedHashSet<>();
        Set<String> structTypes = new LinkedHashSet<>();

        // Excel Sheet
        XSSFWorkbook workbook = new XSSFWorkbook();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(xmlFile);

            var  primitive_list = new ArrayList<Primitives>();
            var arrays_list = new ArrayList<ArrayType>();
            var  structure_list = new ArrayList<Structures>();

            readPrimitives(doc, primitive_list);
            readArrays(doc, arrays_list);
            readStructures(doc, structure_list);

            // for the messages list
            ArrayList<Messages> message_list = new ArrayList<Messages>();
            NodeList messageslist = doc.getElementsByTagName("messages");
            readMessages(messageslist, message_list);

            // for the codec list
            ArrayList<Codecs> codec_list = new ArrayList<Codecs>();
            NodeList codecslist = doc.getElementsByTagName("codecs");
            readCodecs(codecslist, codec_list);

            // cell style for bold text
            CellStyle style = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            style.setFont(font);

            // exporting into excel file
            String primitiveHeader[] = { "Name", "Format", "Type", "SizeInBytes", "SizeInBits" };
            XSSFSheet primitives_sheet = workbook.createSheet("primitives");
            int rowNum = 0;
            rowNum = setHeaderRow(primitives_sheet, rowNum, primitiveHeader, workbook, style);

            for (int i = 0; i < primitive_list.size(); i++) {
                Row row = primitives_sheet.createRow(rowNum++);
                row.createCell(ExcelCellNumbers.C0.getValue()).setCellValue(primitive_list.get(i).getName());
                row.createCell(ExcelCellNumbers.C1.getValue()).setCellValue(primitive_list.get(i).getFormat());
                row.createCell(ExcelCellNumbers.C2.getValue()).setCellValue(primitive_list.get(i).getType());
                row.createCell(ExcelCellNumbers.C3.getValue()).setCellValue(primitive_list.get(i).getSizeInBytes());
                row.createCell(ExcelCellNumbers.C4.getValue()).setCellValue(primitive_list.get(i).getSizeInBits());
                codecFieldType.add(primitive_list.get(i).getName());
                arrayTypes.add(primitive_list.get(i).getName());
                structTypes.add(primitive_list.get(i).getName());

            }

            String structureHeader[] = { "Name", "FieldName", "FieldType", "FieldIndex", "ElementCountField" };
            XSSFSheet structures_sheet = workbook.createSheet("structures");
            rowNum = 0;
            rowNum = setHeaderRow(structures_sheet, rowNum, structureHeader, workbook, style);

            for (int i = 0; i < structure_list.size(); i++) {
                for (int j = 0; j < structure_list.get(i).getStructurefields().size(); j++) {
                    Row row = structures_sheet.createRow(rowNum++);
                    row.createCell(ExcelCellNumbers.C0.getValue()).setCellValue(structure_list.get(i).getName());
                    row.createCell(ExcelCellNumbers.C1.getValue())
                            .setCellValue(structure_list.get(i).getStructurefields().get(j).getName());
                    row.createCell(ExcelCellNumbers.C2.getValue())
                            .setCellValue(structure_list.get(i).getStructurefields().get(j).getType());
                    row.createCell(ExcelCellNumbers.C3.getValue())
                            .setCellValue(structure_list.get(i).getStructurefields().get(j).getFieldIndex());
                    row.createCell(ExcelCellNumbers.C4.getValue()).setCellValue(
                            structure_list.get(i).getStructurefields().get(j).getElementCountStructureField());
                    codecFieldType.add(structure_list.get(i).getName());
                    arrayTypes.add(structure_list.get(i).getName());
                }
            }

            String arrayHeader[] = { "Name", "ElementType", "ConstantElementCount" };
            XSSFSheet arrays_sheet = workbook.createSheet("arrays");
            rowNum = 0;

            rowNum = setHeaderRow(arrays_sheet, rowNum, arrayHeader, workbook, style);

            for (int i = 0; i < arrays_list.size(); i++) {
                Row row = arrays_sheet.createRow(rowNum++);
                row.createCell(ExcelCellNumbers.C0.getValue()).setCellValue(arrays_list.get(i).getName());
                row.createCell(ExcelCellNumbers.C1.getValue()).setCellValue(arrays_list.get(i).getElementType());
                if (arrays_list.get(i).getConstantElementCount() != -1)
                    row.createCell(ExcelCellNumbers.C2.getValue())
                            .setCellValue(arrays_list.get(i).getConstantElementCount());
                codecFieldType.add(arrays_list.get(i).getName());
                structTypes.add(arrays_list.get(i).getName());
            }

            String messagesHeader[] = { "Name", "ID", "FieldName", "FieldIndex", "FieldType", "ElementCountField" };
            XSSFSheet messages_sheet = workbook.createSheet("messages");
            rowNum = 0;

            rowNum = setHeaderRow(messages_sheet, rowNum, messagesHeader, workbook, style);

            for (int i = 0; i < message_list.size(); i++) {
                for (int j = 0; j < message_list.get(i).getMessageFields().size(); j++) {
                    Row row = messages_sheet.createRow(rowNum++);
                    row.createCell(ExcelCellNumbers.C0.getValue()).setCellValue(message_list.get(i).getName());
                    row.createCell(ExcelCellNumbers.C1.getValue()).setCellValue(message_list.get(i).getId());
                    row.createCell(ExcelCellNumbers.C2.getValue())
                            .setCellValue(message_list.get(i).getMessageFields().get(j).getName());
                    row.createCell(ExcelCellNumbers.C3.getValue())
                            .setCellValue(message_list.get(i).getMessageFields().get(j).getfIndex());
                    row.createCell(ExcelCellNumbers.C4.getValue())
                            .setCellValue(message_list.get(i).getMessageFields().get(j).getType());
                    row.createCell(ExcelCellNumbers.C5.getValue())
                            .setCellValue(message_list.get(i).getMessageFields().get(j).getElementCountField());
                    messageNames.add(message_list.get(i).getName());
                }
            }

            String codecsHeader[] = { "Name", "ByteOrder", "CodecPart", "FieldName", "FieldProperty", "FieldIndex",
                    "FieldType", "DefaultValue" };
            XSSFSheet codecs_sheet = workbook.createSheet("codecs");
            int codecRowNum = 0;
            codecRowNum = setHeaderRow(codecs_sheet, codecRowNum, codecsHeader, workbook, style);

            String codecMessagesHeader[] = { "Name", "ByteOrder", "CodecPart", "MessageName" };
            XSSFSheet codec_messages_sheet = workbook.createSheet("codecMessages");
            int codecMessagesRowNum = 0;
            codecMessagesRowNum = setHeaderRow(codec_messages_sheet, codecMessagesRowNum, codecMessagesHeader, workbook,
                    style);

            for (int i = 0; i < codec_list.size(); i++) {

                // HEADER FIELDS
                for (int j = 0; j < codec_list.get(i).getCodecHeader().getHeaderFields().size(); j++) {

                    // create new row
                    Row row = codecs_sheet.createRow(codecRowNum++);
                    row.createCell(ExcelCellNumbers.C0.getValue()).setCellValue(codec_list.get(i).getName());
                    row.createCell(ExcelCellNumbers.C1.getValue()).setCellValue(codec_list.get(i).getByteOrder());
                    row.createCell(ExcelCellNumbers.C2.getValue()).setCellValue("HEADER");
                    row.createCell(ExcelCellNumbers.C3.getValue())
                            .setCellValue(codec_list.get(i).getCodecHeader().getHeaderFields().get(j).getName());
                    row.createCell(ExcelCellNumbers.C4.getValue()).setCellValue(
                            codec_list.get(i).getCodecHeader().getHeaderFields().get(j).getFieldProperty());
                    row.createCell(ExcelCellNumbers.C5.getValue()).setCellValue(
                            codec_list.get(i).getCodecHeader().getHeaderFields().get(j).getFieldIndex());
                    row.createCell(ExcelCellNumbers.C6.getValue())
                            .setCellValue(codec_list.get(i).getCodecHeader().getHeaderFields().get(j).getType());
                    row.createCell(ExcelCellNumbers.C7.getValue()).setCellValue(
                            codec_list.get(i).getCodecHeader().getHeaderFields().get(j).getDefaultValue());

                }
                // FOOTER FIELDS
                for (int j = 0; j < codec_list.get(i).getCodecFooter().getFooterFields().size(); j++) {

                    // create new row
                    Row row = codecs_sheet.createRow(codecRowNum++);
                    row.createCell(ExcelCellNumbers.C0.getValue()).setCellValue(codec_list.get(i).getName());
                    row.createCell(ExcelCellNumbers.C1.getValue()).setCellValue(codec_list.get(i).getByteOrder());
                    row.createCell(ExcelCellNumbers.C2.getValue()).setCellValue("FOOTER");
                    row.createCell(ExcelCellNumbers.C3.getValue())
                            .setCellValue(codec_list.get(i).getCodecFooter().getFooterFields().get(j).getName());
                    row.createCell(ExcelCellNumbers.C4.getValue()).setCellValue(
                            codec_list.get(i).getCodecFooter().getFooterFields().get(j).getFieldProperty());
                    row.createCell(ExcelCellNumbers.C5.getValue()).setCellValue(
                            codec_list.get(i).getCodecFooter().getFooterFields().get(j).getFieldIndex());
                    row.createCell(ExcelCellNumbers.C6.getValue())
                            .setCellValue(codec_list.get(i).getCodecFooter().getFooterFields().get(j).getType());
                    row.createCell(ExcelCellNumbers.C7.getValue()).setCellValue(
                            codec_list.get(i).getCodecFooter().getFooterFields().get(j).getDefaultValue());

                }

                // MESSAGES
                for (int j = 0; j < codec_list.get(i).getCodecMessages().size(); j++) {
                    // create new row
                    Row row = codec_messages_sheet.createRow(codecMessagesRowNum++);
                    row.createCell(ExcelCellNumbers.C0.getValue()).setCellValue(codec_list.get(i).getName());
                    row.createCell(ExcelCellNumbers.C1.getValue()).setCellValue(codec_list.get(i).getByteOrder());
                    row.createCell(ExcelCellNumbers.C2.getValue()).setCellValue("MESSAGES");
                    row.createCell(ExcelCellNumbers.C3.getValue())
                            .setCellValue(codec_list.get(i).getCodecMessages().get(j).getName());
                }
            }

            // to set column size automatically
            int sheetNum = workbook.getNumberOfSheets();
            for (int i = 0; i < sheetNum; i++) {
                setAutoSizedColumns(workbook.getSheetAt(i));
            }

            // data validation and dropdown list
            ArrayList<String> dataTypes = new ArrayList<String>();
            for (Primitives primitive : primitive_list) {
                dataTypes.add(primitive.getName());
            }

            for (ArrayType array : arrays_list) {
                dataTypes.add(array.getName());
            }

            // to target all the column rather than a range of cells
            CellRangeAddressList addressList = new CellRangeAddressList(-1, -1, 6, 6);

            setDataValidationWithList(primitives_sheet,
                    getCellRangeAddress(primitiveHeader, "Format"),
                    getXsdList("XsdFormat"));
            setDataValidationWithList(primitives_sheet,
                    getCellRangeAddress(primitiveHeader, "Type"),
                    getXsdList("XsdPrimitiveType"));

            setDataValidationWithList(arrays_sheet, getCellRangeAddress(arrayHeader,
                    "ElementType"),
                    getDataValidation(arrayTypes));

            setDataValidationWithList(structures_sheet,
                    getCellRangeAddress(structureHeader, "FieldType"),
                    getDataValidation(structTypes));
            setDataValidationWithList(codecs_sheet, getCellRangeAddress(codecsHeader,
                    "FieldProperty"),
                    getXsdList("XsdFieldProperty"));
            String[] part = { "HEADER", "FOOTER" };
            setDataValidationWithList(codecs_sheet, getCellRangeAddress(codecsHeader,
                    "ByteOrder"),
                    getXsdList("XsdByteOrder"));
            setDataValidationWithList(codecs_sheet, getCellRangeAddress(codecsHeader,
                    "CodecPart"), part);
            setDataValidationWithList(codecs_sheet, addressList,
                    getDataValidation(codecFieldType));

            String[] codecMessagePart = { "MESSAGES" };
            setDataValidationWithList(codec_messages_sheet,
                    getCellRangeAddress(codecMessagesHeader, "ByteOrder"),
                    getXsdList("XsdByteOrder"));
            setDataValidationWithList(codec_messages_sheet,
                    getCellRangeAddress(codecsHeader, "CodecPart"),
                    codecMessagePart);
            setDataValidationWithList(codec_messages_sheet,
                    getCellRangeAddress(codecsHeader, "ByteOrder"),
                    getXsdList("XsdByteOrder"));

            setDataValidationWithList(codec_messages_sheet,
                    getCellRangeAddress(codecMessagesHeader, "MessageName"),
                    getDataValidation(messageNames));

            try {
                FileOutputStream outputStream = new FileOutputStream(FILE_NAME);
                workbook.write(outputStream);
                workbook.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println("Done");

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // This method is used to get a string array out of a set of strings 
    public static String[] getDataValidation(Set<String> set) {

        String[] dataValidatinlist = new String[10];
        dataValidatinlist = set.toArray(dataValidatinlist);
        String[] dataTypesArray = set.toArray(new String[0]);
        return dataTypesArray;
    }

    // This method sets auto sized columns for a given sheet
    public static void setAutoSizedColumns(XSSFSheet sheet) {
        Row headerRow = sheet.getRow(0);
        for (int i = 0; i < headerRow.getLastCellNum(); i++) {
            sheet.autoSizeColumn(i);
        }
    }

    // For the validation we have to select which column we want to validate,
    // according to header it's creating an according range of cells
    public static CellRangeAddressList getCellRangeAddress(String[] headerRow, String columnName) {
        // get the index of a string in an array
        int index = 0;
        for (int i = 0; i < headerRow.length; i++) {
            if (headerRow[i].equals(columnName)) {
                index = i;
            }
        }
        // create a range of cells
        // row values choosen as "-1" to taget out entirity of the column
        CellRangeAddressList addressList = new CellRangeAddressList(-1, -1, index, index);
        return addressList;
    }

    // For more definitive expedition header rows has been added to the excel sheet
    public static int setHeaderRow(XSSFSheet sheet, int rowNum, String[] header, XSSFWorkbook workbook,
            CellStyle style) {

        Row row = sheet.createRow(rowNum);

        for (int i = 0; i < header.length; i++) {
            row.createCell(i).setCellValue(header[i]);
            row.getCell(i).setCellStyle(style);
            row.getCell(i).getCellStyle().setLocked(true);
        }
        sheet.createFreezePane(0, 1);
        rowNum += 1;
        return rowNum;
    }

    // Some of the validation elements are coming from xsd file, since xsd is kind
    // of an xml file and the lvl of the list is 3 lvl deep down only took the
    // deepst element as argument
    public static String[] getXsdList(String nodeName) {
        String[] primitiveFormatList;
        try {
            primitiveFormatList = XsdParser.documentReader(XSD_SOURCE_PATH, XsdElementLevels.ELEMENT_1.getElement(),
                    XsdElementLevels.ELEMENT_2.getElement(), XsdElementLevels.ELEMENT_3.getElement(),
                    nodeName);
            return primitiveFormatList;
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // These two methods are used to set data validation
    // The reason that they are seperate functions is formula part is not
    // comprehensive enough
    // Some of the examples has been commented out in the function below
    private static void setDataValidationWithFormula(XSSFSheet sheet, CellRangeAddressList addressList,
            String[] dropDownList) {

        DataValidationHelper validationHelper = new XSSFDataValidationHelper(sheet);
        // validationHelper.createFormulaListConstraint("$B$1:$B$" +
        // (dropDownList.length + 1));
        // DataValidationConstraint constraint =
        // validationHelper.createFormulaListConstraint("$A$2:$A$1048576");
        // DataValidationConstraint constraint =
        // validationHelper.createFormulaListConstraint("UNIQUE($A$2)");

        DataValidationConstraint constraint = validationHelper
                .createFormulaListConstraint("($A$2:$A$23)");

        DataValidation dataValidation = validationHelper.createValidation(constraint, addressList);
        dataValidation.setSuppressDropDownArrow(true);
        dataValidation.getEmptyCellAllowed();
        dataValidation.setShowErrorBox(true);
        sheet.addValidationData(dataValidation);

    }

    private static void setDataValidationWithList(XSSFSheet sheet, CellRangeAddressList addressList,
            String[] dropDownList) {

        DataValidationHelper validationHelper = new XSSFDataValidationHelper(sheet);
        DataValidationConstraint constraint = validationHelper.createExplicitListConstraint(dropDownList);
        DataValidation dataValidation = validationHelper.createValidation(constraint, addressList);
        dataValidation.setSuppressDropDownArrow(true);
        dataValidation.getEmptyCellAllowed();
        dataValidation.setShowErrorBox(true);
        sheet.addValidationData(dataValidation);

    }

    private static void readPrimitives(Document doc, ArrayList<Primitives> primitive_list) {

        NodeList primitiveList = doc.getElementsByTagName("primitive");
        for (int j = 0; j < primitiveList.getLength(); j++) {
            Node q = primitiveList.item(j);
            if (q.getNodeType() == Node.ELEMENT_NODE) {
                Element field = (Element) q;
                primitive_list.add(new Primitives(field.getAttribute("name"),
                        field.getAttribute("format"),
                        Integer.parseInt(field.getAttribute("sizeInBytes")),
                        Integer.parseInt(field.getAttribute("sizeInBits")),
                        field.getAttribute("type")));
            }
        }
    }

    private static void readArrays(Document doc, ArrayList<ArrayType> arrays_list) {
        NodeList arrays = doc.getElementsByTagName("array");
        for (int j = 0; j < arrays.getLength(); j++) {
            Node q = arrays.item(j);
            if (q.getNodeType() == Node.ELEMENT_NODE) {
                Element field = (Element) q;
                if (field.getAttribute("constantElementCount").length() > 0)
                    arrays_list.add(new ArrayType(field.getAttribute("name"),
                            field.getAttribute("elementType"),
                            Integer.parseInt(field.getAttribute("constantElementCount"))));
                else
                    arrays_list.add(new ArrayType(field.getAttribute("name"),
                            field.getAttribute("elementType")));

            }
        }
    }

    private static void readStructures(Document doc, ArrayList<Structures> structure_list) {
        NodeList structures = doc.getElementsByTagName("structure");
        ArrayList<StructureFields> structureField_list = new ArrayList<StructureFields>();
        String name = "";
        for (int j = 0; j < structures.getLength(); j++) {
            Node q = structures.item(j);
            if (q.getNodeType() == Node.ELEMENT_NODE) {
                Element field = (Element) q;
                // System.out.println(field.getAttribute("name"));
                name = field.getAttribute("name");
                readStructureFields(field, structureField_list);
            }
            structure_list.add(new Structures(name, new ArrayList<>(structureField_list)));
            structureField_list.clear();
        }
    }

    private static void readStructureFields(Element field, ArrayList<StructureFields> structureField_list) {
        NodeList structureFields = field.getElementsByTagName("field");
        for (int k = 0; k < structureFields.getLength(); k++) {
            Node r = structureFields.item(k);
            if (r.getNodeType() == Node.ELEMENT_NODE) {
                Element sFields = (Element) r;
                if (sFields.getAttribute("elementCountField").length() > 0)
                    structureField_list.add(new StructureFields(sFields.getAttribute("name"),
                            sFields.getAttribute("type"),
                            Integer.parseInt(sFields.getAttribute("fieldIndex")),
                            sFields.getAttribute("elementCountField")));
                else
                    structureField_list.add(new StructureFields(sFields.getAttribute("name"),
                            sFields.getAttribute("type"),
                            Integer.parseInt(sFields.getAttribute("fieldIndex"))));

            }
        }
    }

    private static void readMessages(NodeList messageslist, ArrayList<Messages> message_list) {

        ArrayList<MessageFields> message_field_list = new ArrayList<MessageFields>();
        String messageName = "";
        String messageID = "";

        for (int i = 0; i < messageslist.getLength(); i++) {
            Node p = messageslist.item(i);
            // System.out.println(p.getNodeName());
            if (p.getNodeType() == Node.ELEMENT_NODE) {
                Element message = (Element) p;
                NodeList fields = message.getElementsByTagName("message");
                // for each message
                for (int j = 0; j < fields.getLength(); j++) {
                    Node q = fields.item(j);
                    if (q.getNodeType() == Node.ELEMENT_NODE) {
                        Element field = (Element) q;
                        // System.out.println(field.getAttribute("id"));
                        messageName = field.getAttribute("name");
                        messageID = field.getAttribute("id");

                        message_field_list = readMessageFields(field, message_field_list);
                    }
                    // TODO: message list is ignoring message eleven cuz it doesnt have any fields
                    message_list.add(new Messages(messageName, Integer.parseInt(messageID),
                            new ArrayList<>(message_field_list)));
                    message_field_list.clear();
                }
            }

        }
    }

    private static ArrayList<MessageFields> readMessageFields(Element field,
            ArrayList<MessageFields> message_field_list) {
        NodeList messageField = field.getElementsByTagName("field");
        for (int k = 0; k < messageField.getLength(); k++) {
            Node r = messageField.item(k);
            if (r.getNodeType() == Node.ELEMENT_NODE) {
                Element messageFields = (Element) r;
                if (messageFields.getAttribute("elementCountField").length() > 0)
                    message_field_list.add(new MessageFields(messageFields.getAttribute("name"),
                            Integer.parseInt(messageFields.getAttribute("fieldIndex")),
                            messageFields.getAttribute("type"),
                            messageFields.getAttribute("elementCountField")));
                else
                    message_field_list.add(new MessageFields(messageFields.getAttribute("name"),
                            Integer.parseInt(messageFields.getAttribute("fieldIndex")),
                            messageFields.getAttribute("type")));

            }
        }
        return message_field_list;
    }

    private static void readCodecs(NodeList codecslist, ArrayList<Codecs> codec_list) {

        for (int i = 0; i < codecslist.getLength(); i++) {

            Node p = codecslist.item(i);
            if (p.getNodeType() == Node.ELEMENT_NODE) {
                Element codec = (Element) p;
                NodeList fields_for_codec = codec.getElementsByTagName("codec");
                for (int j = 0; j < fields_for_codec.getLength(); j++) {
                    String codecName = "";
                    String byteOrder = "";

                    ArrayList<CodecMessage> codecMessages = new ArrayList<CodecMessage>();
                    ArrayList<CodecFields> footerFields = new ArrayList<CodecFields>();
                    ArrayList<CodecFields> headerFields = new ArrayList<CodecFields>();
                    // for codec messages

                    Node q = fields_for_codec.item(j);

                    if (q.getNodeType() == Node.ELEMENT_NODE) {
                        Element field = (Element) q;
                        NodeList messages_for_codec = field.getElementsByTagName("message");

                        codecName = field.getAttribute("name");
                        byteOrder = field.getAttribute("byteOrder");

                        readCodecFields("header", field, headerFields, footerFields);
                        readCodecFields("footer", field, headerFields, footerFields);

                        readCodecMessages(messages_for_codec, codecMessages);
                        codec_list.add(new Codecs(codecName, byteOrder, new CodecHeader(headerFields),
                                new CodecFooter(footerFields), codecMessages));
                    }
                }

            }
        }
    }

    private static void readCodecFields(String nodeName, Element field, ArrayList<CodecFields> headerFields,
            ArrayList<CodecFields> footerFields) {
        // for codec fields
        String fieldName = "";
        String fieldProperty = "";
        String fieldIndex = "";
        String type = "";

        NodeList codecField = field.getElementsByTagName(nodeName);
        for (int k = 0; k < codecField.getLength(); k++) {
            Node r = codecField.item(k);
            if (r.getNodeType() == Node.ELEMENT_NODE) {
                Element header = (Element) r;
                NodeList codecFieldsList = header.getElementsByTagName("field");
                for (int l = 0; l < codecFieldsList.getLength(); l++) {
                    Node z = codecFieldsList.item(l);
                    if (z.getNodeType() == Node.ELEMENT_NODE) {
                        Element codecFieldss = (Element) z;

                        fieldName = codecFieldss.getAttribute("name");
                        fieldProperty = codecFieldss.getAttribute("fieldProperty");
                        fieldIndex = codecFieldss.getAttribute("fieldIndex");
                        type = codecFieldss.getAttribute("type");

                        if (nodeName.equals("header"))
                            headerFields
                                    .add(new CodecFields(fieldName, fieldProperty, Integer.parseInt(fieldIndex), type));
                        else
                            footerFields.add(new CodecFields(fieldName, fieldProperty, Integer.parseInt(fieldIndex),
                                    type));

                    }

                }

            }
        }
    }

    private static void readCodecMessages(NodeList messages_for_codec, ArrayList<CodecMessage> codecMessages) {
        String codecMessageName = "";
        // This particular code snippet is dedicated for messages per codec
        for (int k = 0; k < messages_for_codec.getLength(); k++) {
            Node r = messages_for_codec.item(k);
            if (r.getNodeType() == Node.ELEMENT_NODE) {
                Element message = (Element) r;
                codecMessageName = message.getAttribute("name");
                codecMessages.add(new CodecMessage(codecMessageName));
            }
        }
    }

}
