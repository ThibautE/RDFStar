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
import java.io.FilenameFilter;

import org.openrdf.model.Statement;
import org.openrdf.rio.*;
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


    public static void main(String[] args) throws IOException, RDFHandlerException {
    	
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
		//FILE_DATA = "./src/fichier/500K.rdfxml";
		File fileD = new File(FILE_DATA);
	
		FILE_OUTPUT =  arguments.get(indexO+1);
		//FILE_OUTPUT = "./src/output/";
		
		File fileO = new File(FILE_OUTPUT);
		if(!fileO.exists()) {
			fileO.mkdir();
		}
		
		System.out.println("Jeu de données : " + fileD.getPath());
        System.out.println("Jeu de requêtes : " + fileQ.getPath());
        System.out.println("Fichier de sortie : " + fileO.getCanonicalPath());
        System.out.println("----------------------------------------");

        ArrayList<Query> queries = new ArrayList<Query>(); 
        ArrayList<Query> totalQueries = new ArrayList<Query>();
        //Si le fichier de query est un dossier chercher tous les fichiers finissant par .queryset
        if(fileQ.isDirectory()) {
			FilenameFilter filename = new FilenameFilter() {

				@Override
				public boolean accept(File arg0, String arg1) {
					return arg1.endsWith("queryset");
				}
			};
			
			ArrayList<File> files = new ArrayList<File>(Arrays.asList(fileQ.listFiles(filename)));
			//pour le nombre de fichier trouvé on effectue parseFile
			for(File f : files) {
				queries = parseFile(f.getPath());
				totalQueries.addAll(queries);
			}
			//sinon si c'est un fichier on effectue parseFile seulement sur celui-ci
		}else {
			totalQueries = parseFile(FILE_QUERY);
		}

        if(fileD.isDirectory()) {
			FilenameFilter filename = new FilenameFilter() {

				@Override
				public boolean accept(File arg0, String arg1) {
					return arg1.endsWith("rdfxml");
				}
			};
			
			ArrayList<File> files = new ArrayList<File>(Arrays.asList(fileD.listFiles(filename)));
			for(File f : files) {
				RDFRawParser.readFile(f);
			}
		}else {
	        RDFRawParser.readFile(fileD);
		}

        //Reccuperation du dictionnaire et de l'index
        Dictionary dictionary = RDFRawParser.getDictionary();
        Index index = RDFRawParser.getIndex();
        
        //bind des queries pour predicate et object 
        Query.bind(totalQueries, dictionary);
        long[] durationSolve = new long[4];
        //Resolution des requêtes
        for(int i =0; i<4; i++) {
	        long start3 = System.currentTimeMillis();
	        QuerySolver.solveQueries(totalQueries, index);
	        long end3 = System.currentTimeMillis();
	        durationSolve[i] = end3 - start3;
            System.out.println("Temps total pour la résolution des requêtes : " + durationSolve[i] + "ms (pour " + totalQueries.size() + " requêtes)");
        }
        ExportCSV.exportTime(FILE_OUTPUT, durationSolve);

                        
        System.out.println("----------------------------------------");
        
        //si "-workload_time"
        if (arguments.contains(WORKLOAD_TIME)){
            System.out.println("----------------------------------------");
            System.out.println("Temps total pour la résolution des requêtes : " + durationSolve + "ms (pour " + totalQueries.size() + " requêtes)");
        }
        
        System.out.println("----------------------------------------");
        
        //si "-export_stats"
            if(arguments.contains(EXPORT_STATS)){
	        	System.out.println("Exportation du fichier de stats...");
	            ExportCSV.exportStats(FILE_OUTPUT, totalQueries, dictionary);
            }
        
        //si "-export_results"
            if(arguments.contains(EXPORT_RESULTS)) {
        	System.out.println("Exportation du fichier de resultat...");
        	ExportCSV.exportResults(FILE_OUTPUT, totalQueries, dictionary);
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
    			"	-verbose -------------------------------- Pour avoir les temps d'execution et un fichier de durée d'execution\n" + 
    			"	-workload_timer ------------------------- Pour avoir le temps total d'execution");
    } 
    
    //lit le fichier de reqûete en paramètre (-queries)
	private static ArrayList<Query> parseFile(String fileString) throws IOException {
        File file = new File(fileString);
        
        ArrayList<Query> queryParser = new ArrayList<Query>();
        ArrayList<File> fileQueryParser = new ArrayList<File>();

        if (file.isDirectory()){
        	fileQueryParser.addAll(Arrays.asList(file.listFiles()));
        }else {
        	fileQueryParser.add(file);
        }

        for (File f : fileQueryParser){
            System.out.println("Accès au fichier " + f.getPath());
            queryParser.addAll(Query.parse(f));
        }
        return queryParser;
    }

	
}