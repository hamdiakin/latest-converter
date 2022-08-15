import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

// main class 
public class Main {
    public static void main(String[] args) throws IOException {
        // input path is selected as described in the assignment
        String input_path = ".\\data-files\\easyMessageTest.xml";

        File xmlFile = new File("easyMessageTest.xml");
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        try{
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(input_path);
        } catch(ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            throw new RuntimeException(e);
        }

    }
}