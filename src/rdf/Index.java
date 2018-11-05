package rdf;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.TreeSet;

public class Index {
	
	private HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> ops;
    private HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> pos;

    public Index(){
        this.ops = new HashMap<>();
        this.pos = new HashMap<>();
    }

    public void addToIndex(int sId, int pId, int oId){
        addToPOS(sId, pId, oId);
        addToOPS(sId, pId, oId);
    }

    private void addToPOS(int sId, int pId, int oId){

    	ArrayList<Integer> s = new ArrayList<>();
        HashMap<Integer, ArrayList<Integer>> os = new HashMap<>();

        // Si P existe, recupérer os et s déjà existant.
        if(this.pos.containsKey(pId)){
            os = pos.get(pId);

            if(os.containsKey(oId)){
                s = os.get(oId);
            }
        }

        s.add(sId);
        os.put(oId, s);
        this.pos.put(pId,os);
    }


    private void addToOPS(int sId, int pId, int oId){

        ArrayList<Integer> s;

        ops.computeIfAbsent(oId, k -> new HashMap<>());
        HashMap<Integer, ArrayList<Integer>> o = ops.get(oId);

        o.computeIfAbsent(pId, k -> new ArrayList<>());
        s = o.get(pId);
        HashMap<Integer, ArrayList<Integer>> ps = ops.get(oId);

        // Si P existe, récupérer ps et s déjà existant
        if(this.ops.containsKey(oId)){
            ps = ops.get(oId);
            if(ps.containsKey(pId)){
                s = ps.get(pId);
            }
        }

        s.add(sId);
        ps.put(pId, s);
        this.ops.put(oId,ps);
    }

    public HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> getOPS() {
        return ops;
    }

    public HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> getPOS() {
        return pos;
    }
	/*
	public HashSet<Integer> getLastColumn(int f, int s){
		if()
	}
	*/
	
}