package ob;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class TranslatorSAX {

	private static final String ENTITY = "ENTITY";
	private static final String ATTR = "ATTR";
	private static final String INDEX = "INDEX";
	private static final String RELATION = "RELATION";
	
	public static void main(String[] args) {
		try {
			HashSet<String> targetNodeName = new HashSet<>();
			targetNodeName.add(ENTITY);
			targetNodeName.add(ATTR);
			targetNodeName.add(INDEX);
			targetNodeName.add(RELATION);
			
			Dictionary dictionary = new Dictionary("dictionary.csv");
			
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser = factory.newSAXParser();
			File file = new File("input.edm");
			DefaultHandler handler = new TranslationHandler(dictionary, targetNodeName);
			parser.parse(file, handler);
		} catch (SAXException e) {
			
		} catch (ParserConfigurationException e) {
			
		} catch (IOException e) {
			
		}
	}

}
