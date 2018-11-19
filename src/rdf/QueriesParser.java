package rdf;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class QueriesParser {
	
	//lit le fichier de reqûete en paramètre (-queries)
		static ArrayList<Query> parseFile(String fileString) throws IOException {
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
