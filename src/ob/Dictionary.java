package ob;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Comparator;
import java.util.TreeMap;

public class Dictionary {

	private TreeMap<String, String> dictionaryMap;
	
	/**
	 * 辞書オブジェクトを作る
	 * @param dictionaryFileName
	 */
	public Dictionary(String dictionaryFileName) {
		dictionaryMap = new TreeMap<>(new StringComparator());
		
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
	 * 日本語を snake case の英語に変換する
	 * @param japanese
	 * @return
	 */
	public String translateAll(String japanese) {
		for (String key: dictionaryMap.keySet()) {
			String jap = key;
			String eng = dictionaryMap.get(key) + "_";
			japanese = japanese.replaceAll(jap, eng);
		}
		japanese = japanese.replaceAll("__", "_");
		japanese = japanese.replaceAll("_$", "");
		return japanese;
	}

}

/**
 * 辞書をソートする
 * 長い順
 * @author t-hatano
 *
 */
class StringComparator implements Comparator<String> {
    public int compare(String s1, String s2){
    	int diff = s2.length() - s1.length();
    	if (diff != 0) {
    		return diff;
    	}
    	return s1.compareTo(s2);
    }

} 
