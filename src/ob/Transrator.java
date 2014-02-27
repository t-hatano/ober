package ob;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Transrator {

	private static final String ENTITY = "ENTITY";
	private static final String L_NAME = "L-NAME";
	private static final String P_NAME = "P-NAME";
	private static final String ATTR = "ATTR";
	
	public static void main(String[] args) {
		// 辞書オブジェクトの作成
		HashMap<String, String> dictionaryMap = new HashMap<>();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("dictionary.csv"), "UTF-8"));
			String line;
			while ((line = br.readLine()) != null) {
				line = line.replaceAll("\"", "");
				String[] map = line.split(",");
				String jap, eng;
				jap = map[0];
				eng = (map.length == 2) ? map[1] : "";
				String[] camel = eng.split( "(?<=[a-z])(?=[A-Z])" );
				StringBuilder sb = new StringBuilder(camel[0]);
				for (int i = 1; i < camel.length; i++) {
					sb.append("_");
					sb.append(camel[i]);
				}
				dictionaryMap.put(jap, sb.toString().toUpperCase());
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// XMLファイルを読み込んで，変換する
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			File input = new File("input.edm");

			Node root = builder.parse(input);
			Node edr = root.getFirstChild();
			NodeList list = edr.getChildNodes();
			for (int i = 0; i < list.getLength(); i++) {
				Node n = list.item(i);
				if (n.getNodeName().equals(ENTITY)) {
					NodeList entityList = n.getChildNodes();
					for (int eIndex = 0; eIndex < entityList.getLength(); eIndex++) {
						Node entityChild = entityList.item(eIndex);
						if (entityChild.getNodeName().equals(ATTR)) {
							NamedNodeMap attr = entityChild.getAttributes();
							Node lName = attr.getNamedItem(L_NAME);
							String japanese = lName.getNodeValue();
							List<String> trans = new ArrayList<>();
							while (!japanese.equals("")) {
								String cand = "";
								for (String key: dictionaryMap.keySet()) {
									if (japanese.startsWith(key) && cand.length() < key.length()) {
										cand = key.toString();
									}
								}
								trans.add(cand);
								japanese = japanese.substring(cand.length());
							}
							if (trans.size() == 0) continue;
							StringBuilder english = new StringBuilder(dictionaryMap.get(trans.get(0)));
							for (int j = 1; j < trans.size(); j++) {
								english.append("_");
								english.append(dictionaryMap.get(trans.get(j)));
							}
							Node pName = attr.getNamedItem(P_NAME);
							pName.setNodeValue(english.toString());
						}
					}
				}
			}
			
			File output = new File("output.edm");
			write(output, (Document) root, "UTF-8");
			
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
    private static boolean write(File file, Document document, String encoding) {

        // Transformerインスタンスの生成
        Transformer transformer = null;
        try {
             TransformerFactory transformerFactory = TransformerFactory
                       .newInstance();
             transformer = transformerFactory.newTransformer();
        } catch (TransformerConfigurationException e) {
             e.printStackTrace();
             return false;
        }

        // Transformerの設定
        transformer.setOutputProperty("indent", "yes"); //改行指定
        transformer.setOutputProperty("encoding", encoding); // エンコーディング

        // XMLファイルの作成
        try {
             transformer.transform(new DOMSource(document), new StreamResult(
                       file));
        } catch (TransformerException e) {
             e.printStackTrace();
             return false;
        }

        return true;
   }

}
