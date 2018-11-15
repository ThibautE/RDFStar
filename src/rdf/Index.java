package rdf;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.TreeSet;

public class Index {
	
	private HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> pos;

    public Index(){
        this.pos = new HashMap<>();
    }

    public void addToIndex(int sub, int pred, int obj){
        addPOS(pred, obj, sub);
    }

    void addPOS(int pred, int obj, int sub){
    	ArrayList<Integer> s = new ArrayList<>();
        HashMap<Integer, ArrayList<Integer>> po = new HashMap<>();

        if(this.pos.containsKey(pred)){
            po = pos.get(pred);

            if(po.containsKey(obj)){
                s = po.get(obj);
            }
        }

        s.add(sub);
        po.put(obj, s);
        this.pos.put(pred,po);
    }
    
	/*
	public HashSet<Integer> getLastColumn(int f, int s){
		if()
	}
	*/

    public HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> getPOS() {
        return pos;
    }
	
}