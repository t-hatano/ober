package ob;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Transrator {

	private static final String ENTITY = "ENTITY";
	private static final String L_NAME = "L-NAME";
	private static final String P_NAME = "P-NAME";
	
	public static void main(String[] args) {
		File dictionaly = new File("dictionaly.csv");
		try {
			BufferedReader br = new BufferedReader(new FileReader(dictionaly));
			String line;
			while ((line = br.readLine()) != null) {
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			File input = new File("input.edm");

			File output = new File("output.edm");
			PrintWriter pw = new PrintWriter(output);
			Node root = builder.parse(input);
			Node edr = root.getFirstChild();
			NodeList list = root.getChildNodes();
			for (int i = 0; i < list.getLength(); i++) {
				Node n = list.item(i);
				if (n.getNodeName().equals(ENTITY)) {
					NamedNodeMap attr = n.getAttributes();
					Node lName = attr.getNamedItem(L_NAME);
					NodeList entityList = n.getChildNodes();
					for (int eIndex = 0; eIndex < entityList.getLength(); eIndex++) {
						Node entityChild = entityList.item(eIndex);
					}
				}
			}

			pw.close();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
