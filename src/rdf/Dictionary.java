package rdf;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class Dictionary {
	
	public HashMap<Integer, String> map;
	public HashMap<String, Integer> mapR;
    private static int i = 0;
	
	public Dictionary() {
		super();
		this.map = new HashMap<Integer, String>();
		this.mapR = new HashMap<String, Integer>();
	}

	public HashMap<Integer, String> getDictionary() {
		return map;
	}
	
	public HashMap<String, Integer> getDictionaryReversed() {
		return this.mapR;
	}
	
	public int initDico(String str){
		if (!mapR.containsKey(str)){
            i++;
            this.map.put(i,str);
            this.mapR.put(str, i);
            return i;
        } else {
            return mapR.get(str);
        }
    }

	public int getIntValue(String str) {
		return mapR.getOrDefault(str, -1);
	}
	
	public String getStringValue(Integer integer) {
		return map.get(integer);
	}


}