package rdf;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class Dictionary {
	
	public HashMap<Integer, String> map;
	public HashMap<String, Integer> mapR;
    private static  int cptID = 0;
	
	public Dictionary() {
		super();
		this.map = new HashMap<Integer, String>();
		this.mapR = new HashMap<String, Integer>();
	}

	public Map<Integer, String> getDictionary() {
		return map;
	}
	
	public Map<String, Integer> getDictionaryReversed() {
		return this.mapR;
	}
	
	public int initDico(String str){
		if (!mapR.containsKey(str)){
            cptID++;
            try{
                this.map.put(cptID,str);
                this.mapR.put(str, cptID);

            }catch(Exception e) {
                e.printStackTrace();
            }
            return cptID;
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