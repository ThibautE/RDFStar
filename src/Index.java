import java.util.ArrayList;
import java.util.HashMap;

public class Index {
	
	public HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> pos;
	public HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> ops;

	public Index() {
		this.pos = new HashMap<Integer, HashMap<Integer, ArrayList<Integer>>>();
		this.ops = new HashMap<Integer, HashMap<Integer, ArrayList<Integer>>>();
	}

	public HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> getIndexPOS() {
		return this.pos;
	}
	
	public HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> getIndexOPS() {
		return this.ops;
	}
	
	//POS
	public void addPOS(int p, int o, int s) {
		if(getIndexPOS().get(p) == null) {
			getIndexPOS().put(p, new HashMap<Integer, ArrayList<Integer>>());
			getIndexPOS().get(p).put(o, new ArrayList<Integer>());
		}else if(getIndexPOS().get(p).get(o) == null) {
			getIndexPOS().get(p).put(o, new ArrayList<Integer>());
		}
		getIndexPOS().get(p).get(o).add(s);
	}
	
	//OPS
	public void addOPS(int o, int p, int s) {
		if(getIndexPOS().get(o) == null) {
			getIndexPOS().put(o, new HashMap<Integer, ArrayList<Integer>>());
			getIndexPOS().get(o).put(p, new ArrayList<Integer>());
		}else if(getIndexPOS().get(o).get(p) == null) {
			getIndexPOS().get(o).put(p, new ArrayList<Integer>());
		}
		getIndexPOS().get(o).get(p).add(s);
	}
	
	public void addToIndex(int s, int p, int o){
        addPOS(p, o, s);
        addOPS(o, p, s);
    }
	
}
