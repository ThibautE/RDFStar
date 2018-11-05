package rdf;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import rdf.Query;
import rdf.Dictionary;
import rdf.Triplet;

public class Query {
	
	File fileQuery;
	
	public ArrayList<Triplet> triplet = new ArrayList<Triplet>();
	public static Integer indexS, indexP, indexO;
	public ArrayList<String> var = new ArrayList<String>();
	public ArrayList<Integer> res = new ArrayList<Integer>();
	
	public static HashMap<Integer, String> map;
	public static HashMap<String, Integer> mapR;
	
	public Query(String v) {
		var.addAll(Arrays.asList(v));
	}
	
	public void addTriplet(Triplet t) {
		this.triplet.add(t);
	}
	
	public ArrayList<Triplet> getTriplet() {
		return this.triplet;
	}

    public ArrayList<Integer> getResultQuery(){
        return this.res;
    }
    
    public void setResultQuery(ArrayList<Integer> res){
        this.res = res;
    }

	
	public static void bind(ArrayList<Query> query, Dictionary dictionary) {
		mapR = dictionary.getDictionaryReversed();
		for(Query q : query) {
			for (Triplet t : q.triplet){
				indexP = mapR.get(t.getPredicateString());
				indexO = mapR.get(t.getObjectString());
                String error = "Le triplet " + t + " n'existe pas";
                if (indexP == null){
                    error += " : predicate index";
                }else if(indexO == null) {
                    error += " : object index";
                }else {
                    t.bind(indexP, indexO);
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
	
	public static ArrayList<Query> parse(File file) throws IOException{
		ArrayList<Query> queries = new ArrayList<>();

        String source = readFile(file);

        Pattern fullQueryPattern = Pattern.compile("SELECT (\\S+) WHERE \\{((?:\\n\\s\\1 \\S+ \\S+ \\.)+)? ?\\n?}");

        Matcher matcher = fullQueryPattern.matcher(source);

        Pattern subQueryPattern = Pattern.compile("\\s(\\S+) <(\\S+)> <(\\S+)> \\.");
        Matcher matcher2;

        String subQueries, s, p, o;

        while (matcher.find()){
            s = matcher.group(1);
            subQueries = matcher.group(2);
            matcher2 = subQueryPattern.matcher(subQueries);
            Query query = new Query(s);

            while (matcher2.find()){
                s = matcher2.group(1);
                p = matcher2.group(2);
                o = matcher2.group(3);

                Triplet triplet = new Triplet(s, p, o);
                query.addTriplet(triplet);
            }

            queries.add(query);
        }
        return queries;
	}
	
}
