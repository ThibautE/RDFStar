package rdf;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class QueriesParser {
	
	//lit le fichier de reqûete en paramètre (-queries)
		static ArrayList<Query> parseFile(String fileString) throws IOException {
	        File file = new File(fileString);
	        ArrayList<Query> queryParser = new ArrayList<Query>();
	        System.out.println("Accès au fichier " + fileString);
	        queryParser.addAll(Query.parse(file));
	        return queryParser;
	    }
}
