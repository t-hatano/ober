package ob;

import java.util.HashSet;
import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class TranslationHandler extends DefaultHandler {
	
	private final String P_NAME = "P-NAME";
	private HashSet<String> targetNodeName;
	private Dictionary dictionary;
	
	public TranslationHandler(Dictionary dictionary, HashSet<String> targetNodeName) {
		this.dictionary = dictionary;
		this.targetNodeName = targetNodeName;
	}
	
	public void startDocument() {

	}

	public void endDocument() {

	}

	public void startElement(String namespaceURI, String localName, String qName, Attributes atts) {
//		System.out.println("qName: " + qName);
		
//		if (targetNodeName.contains(qName)) {
//			String jap = atts.getValue(P_NAME);
//			if (jap != null) { 
//				String eng = dictionary.translateAll(jap);
//				System.out.println(eng);
//			}
//		}

	}

	public void endElement(String namespaceURI, String localName, String qName) {

	}

	public void characters(char[] ch, int start, int length) {
		System.out.println(String.valueOf(ch));
//		String str = String.valueOf(ch, start, length);
//		if (target.pop()) {
//			str = dictionary.translateAll(str);
//		}
//		System.out.println(ch);
	}
}
