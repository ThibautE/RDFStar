package rdf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

public class QuerySolver {

	public static int cpt = 0;

	//SolveQueries var parcourir toutes les queries reccupérées
    public static void solveQueries(ArrayList<Query> queries, Index index){
        for (Query query : queries){
            query.setResultQuery(solveQuery(query, index));
        }
    }
     
    private static TreeSet<Integer> solveQuery(Query query, Index index) {
    	HashMap<Integer, ArrayList<Integer>> hash = new HashMap<Integer, ArrayList<Integer>>();
        ArrayList<Integer> array = new ArrayList<Integer>();
        TreeSet<Integer> tree = new TreeSet<Integer>();
        boolean bool = true;      
       
        for (Query.Triplet t : query.getTriplet()){
        	        	
        	hash = index.getPOS().get(t.getPredicateIndex());
        	array = hash.get(t.getObjectIndex());
        	
        	if(array == null) {
        		return new TreeSet<Integer>();
        	}
        	
        	int select = array.size();
        	t.setSelectivite(select);
        	
            if (bool){
            	bool = false;
                tree.addAll(array);
            }else if(tree.isEmpty()){
                break;
            } else  {
                tree.retainAll(array);
            }
        }
        return tree;

    }

}
