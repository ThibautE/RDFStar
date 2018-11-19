package rdf;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.reasoner.rulesys.GenericRuleReasoner;
import com.hp.hpl.jena.reasoner.rulesys.GenericRuleReasonerFactory;
import com.hp.hpl.jena.reasoner.rulesys.Rule;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.util.PrintUtil;

import rdf.QueriesParser;



public class QueryAndReasonOnLocalRDF {
	
	private Model model;
	private ArrayList<File> files;
 
    private String pathToOntology = "src/fichier/100K.rdfxml";
    private String queryFile = "src/query/100/";
 
    private ArrayList<Query> queries;
    ArrayList<Query> totalQueries = new ArrayList<Query>();

 
    public QueryAndReasonOnLocalRDF() {
    	long duree= System.currentTimeMillis();
        System.out.println("Initialisation de RDFEngine...");

        System.out.println("Initialisation de Jena...");
        initJena();
       
        System.out.println("Parsing des requêtes...");
        long d2 = System.currentTimeMillis();
        try {
            loadQueries(queryFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        long dt2 = System.currentTimeMillis()-d2;
        System.out.println("Fin du parsing ("+dt2+")");
 
    }
        
	public ArrayList<Query> parseDir() throws IOException{
		FilenameFilter filename = new FilenameFilter() {

			@Override
			public boolean accept(File arg0, String arg1) {
				return arg1.endsWith("queryset");
			}
		};
		File file = new File("src/query/100/");
		ArrayList<File> files = new ArrayList<File>(Arrays.asList(file.listFiles(filename)));

		ArrayList<Query> queries = new ArrayList<>();
		for (File f : files) {
			queries.addAll(QueriesParser.parseFile(f.toString()));
		}
		return queries;
	}
    
    public void loadQueries(String fileString) throws IOException {
    	this.queries = parseDir();
    }
 
    private void initJena() {
        String patha = "./fichier/100K.rdfxml";
    	this.model = ModelFactory.createDefaultModel();
		InputStream in = FileManager.get().open(patha);
		this.model.read(in, null);
    }
 
    private ArrayList<String> execQueryOnJena(rdf.Query query2) {
        com.hp.hpl.jena.query.Query query = QueryFactory.create(query2.toString());
        QueryExecution qexec = QueryExecutionFactory.create(query, this.model);
        ResultSet rs = qexec.execSelect();
        ArrayList<String> list = new ArrayList<>();
         while (rs.hasNext()) {
            QuerySolution result = rs.nextSolution();
            Pattern pat = Pattern.compile(".*<(.*)>.*");
            Matcher match = pat.matcher(result.toString());
            if(match.find()) {
                list.add(match.group(1));
            }
        }
        return list;
    }
    
    public void compareEngines() {
        
        try {
            loadQueries(pathToOntology);
           
            System.out.println("traitement des requêtes...");
            long qt = System.currentTimeMillis();
            for (Query query : this.queries) {
                ArrayList<String> resJena = execQueryOnJena(query);
            }
            long queryTime = System.currentTimeMillis() - qt;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
 
	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {

		QueryAndReasonOnLocalRDF bm = new QueryAndReasonOnLocalRDF();
		bm.compareEngines();
	}
}
