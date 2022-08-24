package xml2xlsx;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.w3c.dom.Element;

public class xsdReader {
    static String PATH_NAME = ".\\data-files\\TA.xsd";

    public static String[] documentReader(String pathName, String elementLvl1, String elementLvl2, String elementLvl3,
            String type) throws ParserConfigurationException, SAXException, IOException {
        try {
            // parse the document
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(new File(pathName));
            return getXsdInfo(doc, elementLvl1, elementLvl2, elementLvl3, type);
        }
        // if(element3.hasAttributes() &&
        // element3.getAttribute("value").equals("^[a-zA-Z0-9]{1,}$"))
        catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException ed) {
            ed.printStackTrace();
        }
        return null;
    }

    public static String[] getXsdInfo(Document doc, String elementLvl1, String elementLvl2, String elementLvl3,
            String type) {

        NodeList list = doc.getElementsByTagName(elementLvl1);
        ArrayList<String> listOfElements = new ArrayList<String>();
        for (int i = 0; i < list.getLength(); i++) {
            Element element = (Element) list.item(i);
            if (element.hasAttributes() && element.getAttribute("name").equals(type)) {
                NodeList lvl2 = element.getElementsByTagName(elementLvl2);
                for (int j = 0; j < lvl2.getLength(); j++) {
                    Element element2 = (Element) lvl2.item(j);
                    if (element2.hasAttributes()) {
                        NodeList lvl3 = element2.getElementsByTagName(elementLvl3);
                        for (int k = 0; k < lvl3.getLength(); k++) {
                            Element element3 = (Element) lvl3.item(k);
                            if (element3.hasAttributes()) {
                                //System.out.println(element3.getAttribute("value"));
                                listOfElements.add(element3.getAttribute("value"));
                            }
                        }
                    }
                }
            }
        }
        // arraylist to array
        String[] typeList = listOfElements.toArray(new String[0]);

        return typeList;
    }

    /*
     * public static void main(String[] args) throws ParserConfigurationException,
     * SAXException, IOException {
     * documentReader(PATH_NAME, "xsd:simpleType", "xsd:restriction",
     * "xsd:enumeration", "XsdFieldProperty");
     * }
     */
}

/*
 * public static String[] getXsdInfo(Document doc, String elementLvl1, String
 * elementLvl2, String elementLvl3,
 * String type) {
 * 
 * NodeList list = doc.getElementsByTagName(elementLvl1);
 * ArrayList<String> listOfElements = new ArrayList<String>();
 * 
 * NodeList levelThree = doc.getElementsByTagName("xsd:enumeration");
 * for (int k = 0; k < levelThree.getLength(); k++) {
 * Element element3 = (Element) levelThree.item(k);
 * if (element3.hasAttributes()) {
 * System.out.println(element3.getAttribute("value"));
 * }
 * }
 * // arraylist to array
 * String[] typeList = listOfElements.toArray(new String[0]);
 * 
 * return typeList;
 * }
 */