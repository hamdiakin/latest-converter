import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.w3c.dom.Element;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFDataValidationHelper;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class basicIdea {
    private static final String FILE_NAME = "output.xlsx";

    // main method
    public static void main(String[] args) {
        // input path is selected as described in the assignment
        String input_path = ".\\data-files\\easyMessageTest.xml";
        File xmlFile = new File(input_path);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        // Excel Sheet
        XSSFWorkbook workbook = new XSSFWorkbook();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(xmlFile);

            // TODO: Simplify the for loops below
            ArrayList<Primitives> primitive_list = new ArrayList<Primitives>();
            ArrayList<ArrayType> arrays_list = new ArrayList<ArrayType>();
            ArrayList<Structures> structure_list = new ArrayList<Structures>();
            NodeList typeslist = doc.getElementsByTagName("types");
            for (int i = 0; i < typeslist.getLength(); i++) {
                Node p = typeslist.item(i);
                System.out.println(p.getNodeName());
                if (p.getNodeType() == Node.ELEMENT_NODE) {
                    Element type = (Element) p;
                    // tree'den assagi inerken en fazla 3 lvl iniliyor fakatbir lvl asagisinin
                    // boyutunu kontrol ederek ekstra for loop'u engelleyebiliriz
                    // for getting deep down the tree

                    // for primitives
                    NodeList primitives = type.getElementsByTagName("primitive");
                    for (int j = 0; j < primitives.getLength(); j++) {
                        Node q = primitives.item(j);
                        if (q.getNodeType() == Node.ELEMENT_NODE) {
                            Element field = (Element) q;
                            primitive_list.add(new Primitives(field.getAttribute("name"),
                                    field.getAttribute("format"),
                                    field.getAttribute("sizeInBytes"),
                                    field.getAttribute("sizeInBits"),
                                    field.getAttribute("type")));

                            // System.out.println(field.getAttribute("name"));
                        }
                    }

                    // for arrays
                    NodeList arrays = type.getElementsByTagName("array");
                    for (int j = 0; j < arrays.getLength(); j++) {
                        Node q = arrays.item(j);
                        if (q.getNodeType() == Node.ELEMENT_NODE) {
                            Element field = (Element) q;
                            if (field.getAttribute("constantElementCount").length() > 0) {
                                arrays_list.add(new ArrayType(field.getAttribute("name"),
                                        field.getAttribute("elementType"),
                                        field.getAttribute("constantElementCount")));
                            } else {
                                arrays_list.add(new ArrayType(field.getAttribute("name"),
                                        field.getAttribute("elementType")));

                            }
                            System.out.println(field.getAttribute("name"));
                        }
                    }

                    // TODO: for structures, complicated need to solve this

                    NodeList structures = type.getElementsByTagName("structure");
                    ArrayList<StructureFields> structureField_list = new ArrayList<StructureFields>();
                    String name = "";
                    for (int j = 0; j < structures.getLength(); j++) {
                        Node q = structures.item(j);
                        if (q.getNodeType() == Node.ELEMENT_NODE) {
                            Element field = (Element) q;
                            System.out.println(field.getAttribute("name"));
                            name = field.getAttribute("name");
                            NodeList sField = field.getElementsByTagName("field");
                            for (int k = 0; k < sField.getLength(); k++) {
                                Node r = sField.item(k);
                                if (r.getNodeType() == Node.ELEMENT_NODE) {
                                    Element sFields = (Element) r;
                                    System.out.println(sFields.getAttribute("name"));
                                    if (sFields.getAttribute("elementCountField").length() > 0) {
                                        structureField_list.add(new StructureFields(sFields.getAttribute("name"),
                                                sFields.getAttribute("type"),
                                                sFields.getAttribute("fieldIndex"),
                                                sFields.getAttribute("elementCountField")));
                                    } else {
                                        structureField_list.add(new StructureFields(sFields.getAttribute("name"),
                                                sFields.getAttribute("type"), sFields.getAttribute("fieldIndex")));
                                    }
                                }
                            }

                        }
                        structure_list.add(new Structures(name, new ArrayList<>(structureField_list)));
                        structureField_list.clear();
                    }

                }
            }

            // for the messages list
            ArrayList<Messages> message_list = new ArrayList<Messages>();
            ArrayList<MessageFields> message_field_list = new ArrayList<MessageFields>();
            String messageName = "";
            String messageID = "";
            NodeList messageslist = doc.getElementsByTagName("messages");
            for (int i = 0; i < messageslist.getLength(); i++) {
                Node p = messageslist.item(i);
                System.out.println(p.getNodeName());
                if (p.getNodeType() == Node.ELEMENT_NODE) {
                    Element message = (Element) p;
                    /*
                     * System.out.println(message.getAttribute("name"));
                     * messageName = message.getAttribute("name");
                     * messageID = message.getAttribute("id");
                     */
                    NodeList fields = message.getElementsByTagName("message");
                    // for each message
                    for (int j = 0; j < fields.getLength(); j++) {
                        Node q = fields.item(j);
                        if (q.getNodeType() == Node.ELEMENT_NODE) {
                            Element field = (Element) q;
                            // System.out.println(field.getAttribute("id"));
                            messageName = field.getAttribute("name");
                            messageID = field.getAttribute("id");

                            NodeList messageField = field.getElementsByTagName("field");
                            for (int k = 0; k < messageField.getLength(); k++) {
                                Node r = messageField.item(k);
                                if (r.getNodeType() == Node.ELEMENT_NODE) {
                                    Element messageFields = (Element) r;
                                    if (messageFields.getAttribute("elementCountField").length() > 0) {
                                        message_field_list.add(new MessageFields(messageFields.getAttribute("name"),
                                                messageFields.getAttribute("fieldIndex"),
                                                messageFields.getAttribute("type"),
                                                messageFields.getAttribute("elementCountField")));
                                    } else {
                                        message_field_list.add(new MessageFields(messageFields.getAttribute("name"),
                                                messageFields.getAttribute("fieldIndex"),
                                                messageFields.getAttribute("type")));
                                    }

                                }
                            }
                        }
                        // TODO: message list is ignoring message eleven cuz it doesnt have any fields
                        message_list.add(new Messages(messageName, messageID, new ArrayList<>(message_field_list)));
                        message_field_list.clear();
                    }
                }

            }

            // for the codec list
            ArrayList<Codecs> codec_list = new ArrayList<Codecs>();

            NodeList codecslist = doc.getElementsByTagName("codecs");
            for (int i = 0; i < codecslist.getLength(); i++) {

                Node p = codecslist.item(i);
                System.out.println(p.getNodeName());
                // for the messages of the each codec
                if (p.getNodeType() == Node.ELEMENT_NODE) {
                    Element codec = (Element) p;

                    NodeList fields_for_codec = codec.getElementsByTagName("codec");
                    System.out.println(codec.getAttribute("name"));
                    // for each codec
                    for (int j = 0; j < fields_for_codec.getLength(); j++) {
                        String codecName = "";
                        String byteOrder = "";
                        ArrayList<CodecMessage> codecMessages = new ArrayList<CodecMessage>();
                        // for codec fields
                        String fieldName = "";
                        String fieldProperty = "";
                        String fieldIndex = "";
                        String type = "";
                        // footer field list
                        ArrayList<CodecFields> footerFields = new ArrayList<CodecFields>();
                        // header field list
                        ArrayList<CodecFields> headerFields = new ArrayList<CodecFields>();
                        // for codec messages
                        String codecMessageName = "";

                        Node q = fields_for_codec.item(j);

                        if (q.getNodeType() == Node.ELEMENT_NODE) {
                            Element field = (Element) q;

                            // This particular code snippet is dedicated for header fields per codec
                            NodeList header_for_codec = field.getElementsByTagName("header");

                            // This particular code snippet is dedicated for footer fields per codec
                            NodeList footer_for_codec = field.getElementsByTagName("footer");

                            // This particular code snippet is dedicated for messages per codec
                            NodeList messages_for_codec = field.getElementsByTagName("message");

                            // System.out.println(field.getAttribute("name"));
                            codecName = field.getAttribute("name");
                            byteOrder = field.getAttribute("byteOrder");

                            // This particular code snippet is dedicated for header fields per codec
                            for (int k = 0; k < header_for_codec.getLength(); k++) {
                                Node r = header_for_codec.item(k);
                                if (r.getNodeType() == Node.ELEMENT_NODE) {
                                    Element header = (Element) r;
                                    NodeList codecFieldsList = header.getElementsByTagName("field");
                                    for (int l = 0; l < codecFieldsList.getLength(); l++) {
                                        Node z = codecFieldsList.item(l);
                                        if (z.getNodeType() == Node.ELEMENT_NODE) {
                                            Element codecFieldss = (Element) z;
                                            // System.out.println(codecFieldss.getAttribute("name"));
                                            fieldName = codecFieldss.getAttribute("name");
                                            fieldProperty = codecFieldss.getAttribute("fieldProperty");
                                            fieldIndex = codecFieldss.getAttribute("fieldIndex");
                                            type = codecFieldss.getAttribute("type");
                                            headerFields
                                                    .add(new CodecFields(fieldName, fieldProperty, fieldIndex, type));
                                        }

                                    }

                                }
                            }

                            // This particular code snippet is dedicated for footer fields per codec
                            for (int k = 0; k < footer_for_codec.getLength(); k++) {
                                Node r = footer_for_codec.item(k);
                                if (r.getNodeType() == Node.ELEMENT_NODE) {
                                    Element header = (Element) r;
                                    NodeList codecFieldsList = header.getElementsByTagName("field");
                                    for (int l = 0; l < codecFieldsList.getLength(); l++) {
                                        Node z = codecFieldsList.item(l);
                                        if (z.getNodeType() == Node.ELEMENT_NODE) {
                                            Element codecFieldss = (Element) z;
                                            // System.out.println(codecFieldss.getAttribute("name"));
                                            fieldName = codecFieldss.getAttribute("name");
                                            fieldProperty = codecFieldss.getAttribute("property");
                                            fieldIndex = codecFieldss.getAttribute("index");
                                            type = codecFieldss.getAttribute("type");
                                            footerFields
                                                    .add(new CodecFields(fieldName, fieldProperty, fieldIndex, type));
                                        }

                                    }

                                }
                            }

                            // This particular code snippet is dedicated for messages per codec
                            for (int k = 0; k < messages_for_codec.getLength(); k++) {
                                Node r = messages_for_codec.item(k);
                                if (r.getNodeType() == Node.ELEMENT_NODE) {
                                    Element message = (Element) r;
                                    // System.out.println(message.getAttribute("name"));
                                    codecMessageName = message.getAttribute("name");
                                    codecMessages.add(new CodecMessage(codecMessageName));
                                }
                            }

                            // codec objects has to be formed under this line
                            codec_list.add(new Codecs(codecName, byteOrder, new CodecHeader(headerFields),
                                    new CodecFooter(footerFields), codecMessages));
                        }
                    }

                }
            }

            // cell style for bold text
            CellStyle style = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBoldweight(Font.BOLDWEIGHT_BOLD);
            style.setFont(font);

            // exporting into excel file
            String primitiveHeader[] = { "Name", "Format", "Type", "SizeInBytes", "SizeInBits" };

            XSSFSheet primitives_sheet = workbook.createSheet("primitives");
            int rowNum = 0;
            rowNum = setHeaderRow(primitives_sheet, rowNum, primitiveHeader, workbook, style);

            // TODO: set out the text style of the header row

            for (int i = 0; i < primitive_list.size(); i++) {
                Row row = primitives_sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(primitive_list.get(i).getName());
                row.createCell(1).setCellValue(primitive_list.get(i).getFormat());
                row.createCell(2).setCellValue(primitive_list.get(i).getType());
                row.createCell(3).setCellValue(primitive_list.get(i).getSizeInBytes());
                row.createCell(4).setCellValue(primitive_list.get(i).getSizeInBits());

            }

            String structureHeader[] = { "Name", "FieldName", "FieldType", "FieldIndex", "ElementCountStructureField" };
            XSSFSheet structures_sheet = workbook.createSheet("structures");
            rowNum = 0;
            rowNum = setHeaderRow(structures_sheet, rowNum, structureHeader, workbook, style);
            /*
             * for(Structures structure :structure_list)
             *
             */

            for (int i = 0; i < structure_list.size(); i++) {
                for (int j = 0; j < structure_list.get(i).getStructurefields().size(); j++) {
                    Row row = structures_sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(structure_list.get(i).getName());
                    row.createCell(1).setCellValue(structure_list.get(i).getStructurefields().get(j).getName());
                    row.createCell(2).setCellValue(structure_list.get(i).getStructurefields().get(j).getType());
                    row.createCell(3).setCellValue(structure_list.get(i).getStructurefields().get(j).getFieldIndex());
                    row.createCell(4).setCellValue(
                            structure_list.get(i).getStructurefields().get(j).getElementCountStructureField());
                }
            }

            String arrayHeader[] = { "Name", "ElementType", "ElementCountStructureField" };
            XSSFSheet arrays_sheet = workbook.createSheet("arrays");
            rowNum = 0;

            rowNum = setHeaderRow(arrays_sheet, rowNum, arrayHeader, workbook, style);

            for (int i = 0; i < arrays_list.size(); i++) {
                Row row = arrays_sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(arrays_list.get(i).getName());
                row.createCell(1).setCellValue(arrays_list.get(i).getElementType());
                row.createCell(2).setCellValue(arrays_list.get(i).getConstantElementCount());
            }

            String messagesHeader[] = { "Name", "ID", "FieldName", "FieldIndex", "FieldType", "ElementCountField" };
            XSSFSheet messages_sheet = workbook.createSheet("messages");
            rowNum = 0;

            rowNum = setHeaderRow(messages_sheet, rowNum, messagesHeader, workbook, style);

            for (int i = 0; i < message_list.size(); i++) {
                for (int j = 0; j < message_list.get(i).getMessageFields().size(); j++) {
                    Row row = messages_sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(message_list.get(i).getName());
                    row.createCell(1).setCellValue(message_list.get(i).getId());
                    row.createCell(2).setCellValue(message_list.get(i).getMessageFields().get(j).getName());
                    row.createCell(3).setCellValue(message_list.get(i).getMessageFields().get(j).getfIndex());
                    row.createCell(4).setCellValue(message_list.get(i).getMessageFields().get(j).getType());
                    row.createCell(5)
                            .setCellValue(message_list.get(i).getMessageFields().get(j).getElementCountField());
                }
            }

            String codecsHeader[] = { "Name", "ByteOrder", "CodecPart", "FieldName", "FieldProperty", "FieldIndex",
                    "FieldType" };
            XSSFSheet codecs_sheet = workbook.createSheet("codecs");
            int codecRowNum = 0;
            codecRowNum = setHeaderRow(codecs_sheet, codecRowNum, codecsHeader, workbook, style);
            int counter = 0;
            for (int i = 0; i < codec_list.size(); i++) {

                // HEADER FIELDS
                for (int j = 0; j < codec_list.get(i).getCodecHeader().getHeaderFields().size(); j++) {
                    counter = 0;
                    // create new row
                    Row row = codecs_sheet.createRow(codecRowNum++);
                    row.createCell(0).setCellValue(codec_list.get(i).getName());
                    row.createCell(1).setCellValue(codec_list.get(i).getByteOrder());
                    row.createCell(counter + 2).setCellValue("HEADER");
                    row.createCell(counter + 3)
                            .setCellValue(codec_list.get(i).getCodecHeader().getHeaderFields().get(j).getName());
                    row.createCell(counter + 4).setCellValue(
                            codec_list.get(i).getCodecHeader().getHeaderFields().get(j).getFieldProperty());
                    row.createCell(counter + 5).setCellValue(
                            codec_list.get(i).getCodecHeader().getHeaderFields().get(j).getFieldIndex());
                    row.createCell(counter + 6)
                            .setCellValue(codec_list.get(i).getCodecHeader().getHeaderFields().get(j).getType());

                }
                // FOOTER FIELDS
                for (int j = 0; j < codec_list.get(i).getCodecFooter().getFooterFields().size(); j++) {
                    counter = 0;
                    // create new row
                    Row row = codecs_sheet.createRow(codecRowNum++);
                    row.createCell(0).setCellValue(codec_list.get(i).getName());
                    row.createCell(1).setCellValue(codec_list.get(i).getByteOrder());
                    row.createCell(counter + 2).setCellValue("FOOTER");
                    row.createCell(counter + 3)
                            .setCellValue(codec_list.get(i).getCodecFooter().getFooterFields().get(j).getName());
                    row.createCell(counter + 4).setCellValue(
                            codec_list.get(i).getCodecFooter().getFooterFields().get(j).getFieldProperty());
                    row.createCell(counter + 5).setCellValue(
                            codec_list.get(i).getCodecFooter().getFooterFields().get(j).getFieldIndex());
                    row.createCell(counter + 6)
                            .setCellValue(codec_list.get(i).getCodecFooter().getFooterFields().get(j).getType());

                }

                // MESSAGES
                for (int j = 0; j < codec_list.get(i).getCodecMessages().size(); j++) {
                    counter = 0;
                    // create new row
                    Row row = codecs_sheet.createRow(codecRowNum++);
                    row.createCell(0).setCellValue(codec_list.get(i).getName());
                    row.createCell(1).setCellValue(codec_list.get(i).getByteOrder());
                    row.createCell(counter + 2).setCellValue("MESSAGES");
                    row.createCell(counter + 3)
                            .setCellValue(codec_list.get(i).getCodecMessages().get(j).getName());
                }

            }

            // TODO: set autosized columns
            int sheetNum = workbook.getNumberOfSheets();
            for (int i = 0; i < sheetNum; i++) {
                setAutoSizedColumns(workbook.getSheetAt(i));
            }

            // data validation and dropdown list

            ArrayList<String> dataTypes = new ArrayList<String>();
            for(Primitives primitive: primitive_list){
                dataTypes.add(primitive.getName());
            }

            for(ArrayType array: arrays_list){
                dataTypes.add(array.getName());
            }

            String[] dataTypesArray = dataTypes.toArray(new String[0]);

            DataValidation dataValidation = null;
            DataValidationConstraint constraint = null;
            DataValidationHelper validationHelper = null;

            XSSFSheet sheet = (XSSFSheet) workbook.createSheet("sheet");

            validationHelper = new XSSFDataValidationHelper(codecs_sheet);
            CellRangeAddressList addressList = new CellRangeAddressList(1, 67, 6, 6);
            constraint = validationHelper.createExplicitListConstraint(dataTypesArray);
            dataValidation = validationHelper.createValidation(constraint, addressList);
            dataValidation.setSuppressDropDownArrow(true);
            sheet.addValidationData(dataValidation);

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

    public static void setAutoSizedColumns(XSSFSheet sheet) {
        Row headerRow = sheet.getRow(0);
        for (int i = 0; i < headerRow.getLastCellNum(); i++) {
            sheet.autoSizeColumn(i);
        }
    }

    // set header row per sheet
    public static int setHeaderRow(XSSFSheet sheet, int rowNum, String[] header, XSSFWorkbook workbook,
            CellStyle style) {

        Row row = sheet.createRow(rowNum);
        for (int i = 0; i < header.length; i++) {
            row.createCell(i).setCellValue(header[i]);
            row.getCell(i).setCellStyle(style);
        }
        rowNum += 1;
        return rowNum;
    }

}
