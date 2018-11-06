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
import rdf.QuerySolver;

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

    private static String QUERY = "-queries";
    private static String DATA = "-data";
    private static String OUTPUT = "-output";
    private static String VERBOSE = "-verbose";
    private static String EXPORT_RESULTS = "-export_results";
    private static String EXPORT_STATS = "-export_stats";
	private static String WORKLOAD_TIME = "-workload_time";
    private static String HELP = "-help";
    
    private static String FILE_OUTPUT;
    private static String FILE_QUERY ;
    private static String FILE_DATA;


    public static void main(String[] args) throws IOException {
    	
    	int indexQ = -1;
    	int indexD = -1;
    	int indexO = -1;

		List<String> arguments = Collections.unmodifiableList(Arrays.asList(args));
		
		indexQ = arguments.indexOf(QUERY);
		indexD = arguments.indexOf(DATA);
		indexO = arguments.indexOf(OUTPUT);

		if(indexQ < 0 || indexD < 0 || indexO < 0) {
			System.out.println("Erreur! le jeu de requête ou le jeu de données n'est pas renseigné");
			instruction();
		}
		FILE_QUERY = arguments.get(indexQ+1);
		//FILE_QUERY = "./src/query/query.queryset";
		File fileQ = new File(FILE_QUERY);
		
		FILE_DATA =  arguments.get(indexD+1);
		//FILE_DATA = "./src/fichier/100K.rdfxml";
		File fileD = new File(FILE_DATA);
	
		FILE_OUTPUT =  arguments.get(indexO+1);
		//FILE_OUTPUT = "./src/output/";
		File fileO = new File(FILE_OUTPUT);
		
		System.out.println("Jeu de données : " + fileD.getPath());
        System.out.println("Jeu de requêtes : " + fileQ.getPath());
        System.out.println("Fichier de sortie : " + fileO.getCanonicalPath());
        System.out.println("----------------------------------------");

		//timer général
        long start = System.currentTimeMillis();

        //timer parser
        long start1 = System.currentTimeMillis();
        ArrayList<Query> queries = parseFile(FILE_QUERY);
        long end1 = System.currentTimeMillis();
        long durationQuery = end1 - start1;
        
        //timer parser
        long start2 = System.currentTimeMillis();
        RDFRawParser.readFile(fileD);
        long end2 = System.currentTimeMillis();
        long durationParse = end2 - start2;

        Dictionary dictionary = RDFRawParser.getDictionary();
        Index index = RDFRawParser.getIndex();

        Query.bind(queries, dictionary);

        //timer requête
        long start3 = System.currentTimeMillis();
        QuerySolver.solveQueries(queries, index);
        long end3 = System.currentTimeMillis();
        long durationSolve = end3 - start3;

        long end = System.currentTimeMillis();
        long totalDuration = end - start;
                        
        System.out.println("----------------------------------------");
        
        //si "-workload_time"
        if (arguments.contains(WORKLOAD_TIME)){
            System.out.println("----------------------------------------");
            System.out.println("Temps total pour l'execution du programme : " + totalDuration + "ms (~" + totalDuration/1000 + " seconde(s))");
        }
        
        System.out.println("----------------------------------------");

      //si "-verbose"
        if(arguments.contains(VERBOSE)) {
            System.out.println("Différentes durée des tâches : ");
            System.out.println("Parser effectué en " + durationParse + " ms");
            System.out.println(queries.size() + " requête en " + durationQuery + "ms");
            System.out.println("Query effectué en " + durationSolve + "ms");
            System.out.println("----------------------------------------");
            System.out.println("Exportation du fichier des temps d'execution...");

            ExportCSV.exportTime(FILE_OUTPUT, durationParse, durationQuery, durationSolve, totalDuration);
        }
        
        //si "-export_stats"
        if(arguments.contains(EXPORT_STATS)){
        	System.out.println("Exportation du fichier de stats...");
            ExportCSV.exportStats(FILE_OUTPUT, queries,dictionary);
        }
        
        //si "-export_results"
        if(arguments.contains(EXPORT_RESULTS)) {
        	System.out.println("Exportation du fichier de resultat...");
        	ExportCSV.exportResults(FILE_OUTPUT, queries, dictionary);
        }
        
        if(arguments.contains(HELP)) {
        	instruction();
        }
       
    }
    
    public static void instruction() {
    	System.out.println("Voici les différentes commande possible avec le programme\n"
    			+ "-queries \"/chemin/vers/requetes\" ----------- OBLIGATOIRE \n" + 
    			"	-data \"/chemin/vers/donnees\" ------------ OBLIGATOIRE \n" + 
    			"	-output \"/chemin/vers/dossier/sortie\" --- CONSEILLE \n" + 
    			"	-export_results ------------------------- Pour avoir le fichier de resultat (emplacement -output)\n" + 
    			"	-export_stats --------------------------- Pour avoir le fichier de statistique (emplacement -output)\n" + 
    			"	-verbose -------------------------------- Pour avoir les temps d'execution\n" + 
    			"	-workload_timet ------------------------- Pour avoir le temps total d'execution");
    } 
    
    //lit le fichier de reqûete en paramètre (-queries)
	private static ArrayList<Query> parseFile(String fileString) throws IOException {
        File fileS = new File(fileString);

        ArrayList<Query> query = new ArrayList<Query>();
        ArrayList<File> file = new ArrayList<File>();

        if (fileS.isDirectory()){
        	file.addAll(Arrays.asList(fileS.listFiles()));
        }else {
        	file.add(fileS);
        }

        for (File f : file){
            System.out.println("Lecture du fichier " + f.getPath());
            query.addAll(Query.parse(f));
        }
        return query;
    }

	
}