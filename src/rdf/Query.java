package rdf;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeSet;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import rdf.Query;
import rdf.Dictionary;

public class Query {
	
	public static class Triplet {

	    private String s, p, o;
	    private Integer indexS, indexP, indexO;
	    private int select;

	    Triplet(String subject, String property, String object){
	        s = subject;
	        p = property;
	        o = object;
	    }


	    String getPredicate(){
	        return p;
	    }

	    Integer getPredicateIndex(){
	        return indexP;
	    }
	    
	    String getObject(){
	        return o;
	    }
	    
	    Integer getObjectIndex(){
	        return indexO;
	    }

	    String getSubject(){
	        return o;
	    }
	    
	    Integer getSubjectIndex() {
	    	return indexS;
	    }

	    public int getSelectivite() {
	        return select;
	    }

	    void setSelectivite(int s) {
	        this.select = s;
	    }

	    void bind(Integer p, Integer o){
	        indexO = o;
	        indexP = p;
	    }

	    public String toString(){
	            return s + " " + p + " " + o;
	    }
	}
	
	File fileQuery;
	
	public ArrayList<Triplet> triplet = new ArrayList<Triplet>();
	public static Integer indexS, indexP, indexO;
	public ArrayList<String> arrayVar = new ArrayList<String>();
	public TreeSet<Integer> treeResult = new TreeSet<Integer>();

	public Query(String v) {
		arrayVar.addAll(Arrays.asList(v));
	}
	
	public void addTriplet(Triplet t) {
		this.triplet.add(t);
	}
	
	public ArrayList<Triplet> getTriplet() {
		return this.triplet;
	}

    public TreeSet<Integer> getResultQuery(){
        return treeResult;
    }
    
    public void setResultQuery(TreeSet<Integer> res){
        treeResult = res;
    }

	
	public static void bind(ArrayList<Query> query, Dictionary dictionary) {
		Map<String, Integer> dicoReverse = dictionary.getDictionaryReversed();
        Integer sId, oId, pId;


        for (Query q : query){
            for (Triplet t : q.triplet){

                pId = dicoReverse.get(t.getPredicate());
                oId = dicoReverse.get(t.getObject());

                if (pId == null || oId == null){
                    String errMessage = "aucune solution trouvée : ";
                    if (pId == null) errMessage += "aucun Predicate ne correspond";
                    if (oId == null) errMessage += "aucun Object ne correspond";
                    throw new NullPointerException(errMessage);
                } else {
                    t.bind(pId, oId);
                }

            }

        }
	}

	private static String readFile(File inputFile) throws IOException {
        StringBuilder sb = new StringBuilder();
        Stream<String> stream =
                Files.lines( Paths.get(inputFile.toURI()), StandardCharsets.UTF_8);
        stream.forEach(s -> sb.append(s).append("\n"));
        return sb.toString();
    }
	
	public static ArrayList<Query> parse(File inputFile) throws IOException {
        ArrayList<Query> queries = new ArrayList<>();

        String source = readFile(inputFile);

        Pattern fullQueryPattern = Pattern.compile("SELECT (\\S+) WHERE \\{((?:\\n\\s\\1 \\S+ \\S+ \\.)+)? ?\\n?}");

        Matcher fullQueryMatcher = fullQueryPattern.matcher(source);

        Pattern subQueryPattern = Pattern.compile("\\s(\\S+) <(\\S+)> <(\\S+)> \\.");
        Matcher subQueryMatcher;

        String subQueries, s, p, o;

        while (fullQueryMatcher.find()){
            s = fullQueryMatcher.group(1);
            subQueries = fullQueryMatcher.group(2); 
            subQueryMatcher = subQueryPattern.matcher(subQueries);
            Query query = new Query(s);

            while (subQueryMatcher.find()){
                s = subQueryMatcher.group(1);
                p = subQueryMatcher.group(2);
                o = subQueryMatcher.group(3);

                Triplet triplet = new Query.Triplet(s, p, o);
                query.addTriplet(triplet);
            }

            queries.add(query);
        }

        return queries;
    }
	
	public String toString(){

        StringBuilder sb = new StringBuilder();
        sb.append("SELECT ");
        for (String select : arrayVar){
            sb.append(select).append(' ');
        }
        sb.append("WHERE {\n");
        for (Triplet where : triplet){
            sb.append(where.toString()).append('\n');
        }
        sb.append('}');
        return sb.toString();
    }
}