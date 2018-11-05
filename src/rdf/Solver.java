package rdf;

import java.util.ArrayList;
import java.util.HashMap;

import rdf.Query;
import rdf.Index;
import rdf.Triplet;

public class Solver {
	public static void solveQueries(ArrayList<Query> queries, Index index){

        for (Query q : queries){
            q.setResultQuery(solveQuery(q, index));
        }

    }

    private static ArrayList<Integer> solveQuery(Query query, Index index) {

        boolean insert = true;
        ArrayList<Integer> que = new ArrayList<Integer>();
        
        for (Triplet t : query.getTriplet()){
            ArrayList<Integer> subMatches = solveTriplet(t, index);

            if (insert){
            	que.addAll(subMatches);
            	insert = false;
            }else if(que.isEmpty()){
                break;
            } else  {
            	que.retainAll(subMatches);
            }
        }

        return que;

    }

    private static ArrayList<Integer> solveTriplet(Triplet triplet, Index index) {
        HashMap<Integer, ArrayList<Integer>> os = index.getIndexPOS().get(triplet.getPredicateIndex());

        ArrayList<Integer> s = os.get(triplet.getObjectIndex());

        if (s == null){
            return new ArrayList<Integer>();
        }

        int selectivity = s.size();
        triplet.setSelect(selectivity);

        return s;
    }

}
