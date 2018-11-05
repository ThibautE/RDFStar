package rdf;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.openrdf.model.Statement;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.Rio;
import org.openrdf.rio.*;
import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.helpers.RDFHandlerBase;

import rdf.RDFRawParser;
import rdf.Query;
import rdf.Dictionary;
import rdf.Index;
import rdf.Solver;
import rdf.Triplet;

public class Main {
	
	/*
	 java -jar rdfstar
	-queries "/chemin/vers/requetes"
	-data "/chemin/vers/donnees"
	-output "/chemin/vers/dossier/sortie"
	-verbose
	-export_results
	-export_stats
	-workload_time
	 */

    private static String HELP = "-help";
    private static String QUERY = "-queries";
    private static String DATA = "-data";
    private static String OUTPUT = "-output";
    private static String VERBOSE = "-verbose";
    private static String EXPORT_RESULTS = "-export_results";
    private static String EXPORT_STATS = "-export_stats";
	private static String WORKLOAD_TIME = "-workload_time";
    
    private static String FILE_OUTPUT;
    private static String FILE_QUERY ;
    private static String FILE_DATA;


    public static void main(String[] args) throws IOException {

		List<String> arguments = Collections.unmodifiableList(Arrays.asList(args));
		//arguments.add(0, "-workload_time");
		
		int indexQuery = arguments.indexOf(QUERY);
		FILE_QUERY = arguments.get(indexQuery+1);
		//FILE_QUERY = "./src/query/query.queryset";
		File fileQ = new File(FILE_QUERY);

		int indexData = arguments.indexOf(DATA);
		FILE_DATA =  arguments.get(indexData+1);
		//FILE_DATA = "./src/fichier/100K.rdfxml";
		File fileD = new File(FILE_DATA);
		
		int indexOutput = arguments.indexOf(OUTPUT);
		//if(indexOutput<0 || new File(arguments.get(indexOutput+1)).isDirectory()) {
			FILE_OUTPUT =  arguments.get(indexOutput+1);
			//FILE_OUTPUT = "./src/output/";
			File fileO = new File(FILE_OUTPUT);

		//}
		
		if(FILE_QUERY.isEmpty()) {
			System.out.println("Erreur! aucune requête renseigné, ajoutez avec la commande -queries \"Chemin jusqu'au dossier ou fichier\"");
		}
		if(FILE_DATA.isEmpty()) {
			System.out.println("Erreur! aucune requête renseigné, ajoutez avec la commande -data \"Chemin jusqu'au fichier\"");
		}
		if(FILE_OUTPUT.isEmpty()) {
			System.out.println("Erreur! aucune requête renseigné, ajoutez avec la commande -output \"Chemin jusqu'au dossier ou fichier\"");
		}

        long start = System.currentTimeMillis();

        long start1 = System.currentTimeMillis();
        ArrayList<Query> queries = parseQueries(FILE_QUERY);
        long end1 = System.currentTimeMillis();
        long durationQuery = end1 - start1;

        long start2 = System.currentTimeMillis();
        RDFRawParser.readFile(fileD);
        long end2 = System.currentTimeMillis();
        long durationParse = end2 - start2;

        Dictionary dico = RDFRawParser.getDictionary();
        Index index = RDFRawParser.getIndex();

        Query.bind(queries, dico);

        long start3 = System.currentTimeMillis();
        Solver.solveQueries(queries, index);
        long end3 = System.currentTimeMillis();
        long durationSolve = end3 - start3;

        long end = System.currentTimeMillis();
        long totalDuration = end - start;
        
        System.out.println("----------------------------------------");

        if(arguments.contains(EXPORT_STATS)){
        	System.out.println("Exportation du fichier de stats");
            exportStats(queries,dico);
        }
        
        if(arguments.contains(EXPORT_RESULTS)) {
        	System.out.println("Exportation du fichier de resultat");
            exportResults(queries, dico);
        }
        
        if (arguments.contains(WORKLOAD_TIME)){
            System.out.println("----------------------------------------");
            System.out.println("Temps total pour l'execution du programme : " + totalDuration + "ms");
            System.out.println("----------------------------------------");
        }
        
        System.out.println("----------------------------------------");

        if(arguments.contains(VERBOSE)) {
        	System.out.println("Input data file : " + fileD.getPath());
            System.out.println("Input queries file : " + fileQ.getPath());
            System.out.println("Output directory: " + fileO.getPath());
            System.out.println("----------------------------------------");
            System.out.println("Différentes durée des tâches : ");
            System.out.println("Parser effectué en " + durationParse + " ms");
            System.out.println(queries.size() + " requête en " + durationQuery + "ms");
            System.out.println("Query effectué en " + durationSolve + "ms");
        }
    }
    
	private static ArrayList<Query> parseQueries(String fileString) throws IOException {
        
        File file = new File(fileString);

        ArrayList<Query> query = new ArrayList<Query>();
        ArrayList<File> files = new ArrayList<File>();

        if (file.isDirectory()){
            files.addAll(Arrays.asList(file.listFiles()));
        }else {
            files.add(file);
        }


        for (File f : files){
            System.out.println("Lecture du fichier " + f.getCanonicalPath());
            query.addAll(Query.parse(f));
        }
        return query;
    }


    private static void exportStats(ArrayList<Query> queries, Dictionary dico) throws IOException {
    	File fileO = new File(FILE_OUTPUT); 
        FileWriter stats = new FileWriter(fileO.getPath() + "/" + "stats.csv");
        stats.append("Nom;Correspondances;Selectivité(%)" + "\n");
        for(Query q : queries){
            for(Triplet triplet : q.getTriplet()){
                stats.append(triplet.toString()).append(";");
                stats.append(String.valueOf(triplet.getSelect()) + ";");

                float selectivity = ((float) triplet.getSelect() / dico.getDictionary().size()) * 100 ;
                stats.append(String.valueOf(selectivity) + "\n");
            }
        }

        stats.close();
    }

    private static void exportResults(ArrayList<Query> queries, Dictionary dico) throws IOException {
    	File fileO = new File(FILE_OUTPUT); 
        FileWriter result = new FileWriter(fileO.getPath() + "/" + "result.csv");
        result.append("P;O;S" + "\n");
        for(Query q : queries){
            result.append(q.toString()).append(';');
            for ( Integer resId : q.getResultQuery() ){
                result.append( dico.getDictionary().get(resId) ).append(';');
            }
            result.append('\n');
        }
        result.close();
    }
}
