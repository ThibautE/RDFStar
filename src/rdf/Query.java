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
	            return s + " <" + p + "> <" + o + ">";
	    }
	}
		
	public ArrayList<Triplet> triplet = new ArrayList<Triplet>();
	public static Integer indexS, indexP, indexO;
    private boolean check = false;
	public ArrayList<String> arrayVar = new ArrayList<String>();
	public TreeSet<Integer> treeResult = new TreeSet<Integer>();

	public Query(String... v) {
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
    
    boolean checking() {
        return check;
    }
    
    private void checked() {
        check = true;
    }
    
    private void unchecked() {
    	check = false;
    }
	
	public static void bind(ArrayList<Query> query, Dictionary dictionary) {
		HashMap<String, Integer> mapR = dictionary.getDictionaryReversed();
        Integer obj, pred;
        for (Query q : query){
            for (Triplet t : q.triplet){
                pred = mapR.get(t.getPredicate());
                obj = mapR.get(t.getObject());
                if (pred == null || obj == null){
                    q.checked();
                    break;
                } else {
                    t.bind(pred, obj);
                }
            }
        }
	}
	
	public static ArrayList<Query> parse(File file) throws IOException {
		String sub, pred, obj;
        ArrayList<Query> queries = new ArrayList<>();
        
        //lecture du fichier
        StringBuilder sb = new StringBuilder();
        Stream<String> stream = Files.lines(Paths.get(file.toURI()), StandardCharsets.UTF_8);
        stream.forEach(s -> sb.append(s).append("\n"));
       
        String fileString = sb.toString();
        stream.close();
        
        Pattern pattern = Pattern.compile("SELECT (\\S+) WHERE \\{((?:\\n\\t?\\s\\1 \\S+ \\S+ ?> ?\\.?)+)? ?\\.?\\n*}");
        //triplet spo
        Pattern pat = Pattern.compile("\\s(\\S+) <(\\S+)> <(\\S+)> \\.");

        //on applique notre patern au fichier
        Matcher matcher = pattern.matcher(fileString);
        Matcher match;

        String res; 

        while (matcher.find()){
            sub = matcher.group(1);
            res = matcher.group(2); 
            match = pat.matcher(res);
            Query query = new Query(sub);
            while (match.find()){
                sub = match.group(1);
                pred = match.group(2);
                obj = match.group(3);
                Triplet triplet = new Query.Triplet(sub, pred, obj);
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