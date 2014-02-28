package ob;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Stack;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.OutputKeys;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Translator {

	private static final String ENTITY = "ENTITY";
	private static final String P_NAME = "P-NAME";
	private static final String ATTR = "ATTR";
	private static final String INDEX = "INDEX";
	private static final String RELATION = "RELATION";
	private static final String ENCODING = "UTF-8";
	private static final String DEFAULT_OUTPUT = "output.edm";
	
	private static Dictionary dictionary;
	
	/**
	 * 日本語のモデルを英語のモデルに変換する
	 * @param args: {辞書ファイル, 日本語のモデル, [出力先]}
	 */
	public static void main(String[] args) {
		// 引数のチェックと入力ファイル名のチェック
		if (args.length != 2 && args.length != 3) {
			System.err.println("usage: dictionary_file.csv japanese_model.edm [english_model.edm]");
			return;
		}
		if (!args[0].endsWith(".csv")) {
			System.err.println(args[0] + ": Not CSV");
		}
		if (!args[1].endsWith(".edm")) {
			System.err.println(args[1] + ": Not EDM");
		}
		
		dictionary = new Dictionary(args[0]);
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			HashSet<String> targetNodeName = new HashSet<>();
			targetNodeName.add(ENTITY);
			targetNodeName.add(ATTR);
			targetNodeName.add(INDEX);
			targetNodeName.add(RELATION);
			
			DocumentBuilder builder = factory.newDocumentBuilder();
			File input = new File(args[1]);
			Node root = builder.parse(input);
			Node edr = root.getFirstChild();
			
			Stack<Node> stack = new Stack<>();
			stack.add(edr);
			while (!stack.isEmpty()) {
//				Node n = stack.peek();
				Node n = stack.pop();
				if (targetNodeName.contains(n.getNodeName())) {
					nodeTranslator(n);
				}
				NodeList children = n.getChildNodes();
				for (int i = 0; i < children.getLength(); i++) {
					stack.add(children.item(i));
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
	 * @return
	 */
	private static void nodeTranslator(Node n) {
		NamedNodeMap attr = n.getAttributes();
		Node pName = attr.getNamedItem(P_NAME);
		if (pName == null) return;
		String japanese = pName.getNodeValue();
		String english = dictionary.translateAll(japanese);
		pName.setNodeValue(english.toString());
	}

	/**
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
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, encoding);
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        
        // XMLファイルの作成
        try {
             transformer.transform(new DOMSource(document), new StreamResult(file));
        } catch (TransformerException e) {
             e.printStackTrace();
             return false;
        }

        return true;
   }

}
