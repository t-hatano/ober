package ob;

import java.io.File;
import java.io.IOException;
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

public class Translator {

	private static final String ENTITY = "ENTITY";
	private static final String L_NAME = "L-NAME";
	private static final String P_NAME = "P-NAME";
	private static final String ATTR = "ATTR";
	private static final String ENCODING = "UTF-8";
	private static final String DEFAULT_OUTPUT = "output.edm";
	
	private static Dictionary dictionary;
	
	/**
	 * 日本語のモデルを英語のモデルに変換する
	 * @param args: {辞書ファイル, 日本語のモデル, [出力先]}
	 */
	public static void main(String[] args) {
		if (args.length != 2 && args.length != 3) {
			System.err.println("usage: dictionary_file.csv japanese_model.edm [english_model.edm]");
			return;
		}
		
		dictionary = new Dictionary(args[0]);
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			File input = new File(args[1]);

			Node root = builder.parse(input);
			Node edr = root.getFirstChild();
			NodeList list = edr.getChildNodes();
			for (int i = 0; i < list.getLength(); i++) {
				Node n = list.item(i);
				if (n.getNodeName().equals(ENTITY)) {
					nodeTranslator(n);
					NodeList entityList = n.getChildNodes();
					for (int eIndex = 0; eIndex < entityList.getLength(); eIndex++) {
						Node entityChild = entityList.item(eIndex);
						if (entityChild.getNodeName().equals(ATTR)) {
							nodeTranslator(entityChild);
						}
					}
				}
			}
			
			String outputFileName = (args.length == 3) ? args[2] : DEFAULT_OUTPUT;
			File output = new File(outputFileName);
			if (write(output, (Document) root, ENCODING)) {
				System.out.println("success");
			} else {
				System.out.println("failed");
			}
			
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * ノード中のP_NAMEを英語に変換する
	 * @param n
	 * @return true : 変換できた
	 *          false: 変換失敗 
	 */
	private static boolean nodeTranslator(Node n) {
		NamedNodeMap attr = n.getAttributes();
		Node lName = attr.getNamedItem(L_NAME);
		String japanese = lName.getNodeValue();
		List<String> trans = dictionary.transList(japanese);
		if (trans.size() == 0) {
			return false;
		}
		
		String english = constructSnakeCase(trans);
		Node pName = attr.getNamedItem(P_NAME);
		pName.setNodeValue(english.toString());
		return true;
	}
	
	private static String constructSnakeCase(List<String> trans) {
		StringBuilder str = new StringBuilder(trans.get(0));
		for (int j = 1; j < trans.size(); j++) {
			String t = trans.get(j);
			if (t.equals("")) continue;
			str.append("_");
			str.append(t);
		}
		return str.toString();
	}
	
	/**
	 * 借り物
	 * xmlファイルを出力する
	 * @param file
	 * @param document
	 * @param encoding
	 * @return
	 */
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
