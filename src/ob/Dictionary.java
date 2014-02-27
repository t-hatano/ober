package ob;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Dictionary {

	private HashMap<String, String> dictionaryMap = new HashMap<>();
	
	/**
	 * �����I�u�W�F�N�g�����
	 * @param dictionaryFileName
	 */
	public Dictionary(String dictionaryFileName) {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(dictionaryFileName), "UTF-8"));
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
	}
	
	/**
	 * japanese�̐擪����C�����ɂ���P��̂����Œ��̂��̂𒊏o����
	 * @param japanese
	 * @return
	 */
	public String extractBeginWord(String japanese) {
		String cand = "";
		for (String key: dictionaryMap.keySet()) {
			if (japanese.startsWith(key) && cand.length() < key.length()) {
				cand = key.toString();
			}
		}
		return cand;
	}
	
	/**
	 * ���{��̒P����p��ɕϊ�����
	 * @param word
	 * @return
	 */
	public String translate(String word) {
		return dictionaryMap.get(word);
	}
	
	/**
	 * ���{��� snake case �̉p��ɕϊ�����
	 * @param japanese
	 * @return
	 */
	public List<String> transList(String japanese) {
		List<String> trans = new ArrayList<>();
		String prev, next;
		prev = next = japanese;
		while (!next.equals("")) {
			String word = extractBeginWord(next);
			trans.add(translate(word));
			prev = next;
			next = prev.substring(word.length());
			if (next.length() > prev.length()) {
				break;
			}
		}
		return trans;
	}
}
