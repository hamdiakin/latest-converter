import java.io.File;
import java.io.IOException;
import java.util.Hashtable;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.w3c.dom.Element;

public class basicIdeav2 {
    // deneme methodu
    private static void recursiveDoc(NodeList list, String[] tagNameList) {
        boolean isLeaf = false;
        while (!isLeaf) {
            if (list.getLength() == 0) {
                isLeaf = true;
                break;
            } else {
                for (int i = 0; i < list.getLength(); i++) {
                    Node node = list.item(i);
                    System.out.println(node.getNodeName());
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        Element element = (Element) node;
                        // System.out.println(element.getAttribute(tagNameList[tagCounter]));
                        if (element.getElementsByTagName("structure").getLength() > 0) {
                            nodeLoop(list);
                            list = element.getElementsByTagName("field");
                            recursiveDoc(list, tagNameList);
                        } else {
                            nodeLoop(list);
                            isLeaf = true;
                            break;
                        }
                    }
                }
            }
        }
    }

    private static void nodeLoop(NodeList list) {
        for (int j = 0; j < list.getLength(); j++) {
            Node childNode = list.item(j);
            if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                Element childElement = (Element) childNode;
                String input = "name";
                System.out.println(childElement.getAttribute(input));
            }
        }
    }

    // main method
    public static void main(String[] args) {

        // input path is selected as described in the assignment
        String input_path = ".\\data-files\\easyMessageTest.xml";
        File xmlFile = new File(input_path);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(xmlFile);

            // New way to implementing the loops
            Hashtable<String, String[]> my_dict = new Hashtable<String, String[]>();
            my_dict.put("primitive", new String[] { "name", "format" });

            // TODO: Simplify the for loops
            // for the type list
            String[] tagNameList = { "primitive", "array", "structure" };
            NodeList typeslist = doc.getElementsByTagName("types");
            recursiveDoc(typeslist, tagNameList);

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
