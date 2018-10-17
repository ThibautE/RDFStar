import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Collections;

public class Dictionary {
	
	public HashMap<Integer, String> map;
	
	public Dictionary() {
		super();
		map = new HashMap<Integer, String>();
	}

	public HashMap<Integer, String> getDictionary() {
		return map;
	}
	
	public Dictionary init(HashSet<String> words) {
		int n = 0;
		List<String> list = new ArrayList<String>(words);
		Collections.sort(list);
		for (String word : list) {
			n++;
			this.getDictionary().put(n, word); 
		}
		return this;
	}

}
