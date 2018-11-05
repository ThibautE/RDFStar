package rdf;
import java.util.HashMap;

public class Dictionary {
	
	public HashMap<Integer, String> map;
	public HashMap<String, Integer> mapR;
	
	public Dictionary() {
		super();
		this.map = new HashMap<Integer, String>();
		this.mapR = new HashMap<String, Integer>();
	}

	public HashMap<Integer, String> getDictionary() {
		return this.map;
	}
	
	public HashMap<String, Integer> getDictionaryReversed() {
		return this.mapR;
	}
	
	public int initDico(String str){
		int n = 0;
        if (!mapR.containsKey(str)){
            n++;
            this.map.put(n, str);
            this.mapR.put(str, n);
            return n;
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
