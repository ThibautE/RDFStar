package rdf;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import org.openrdf.model.Statement;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.Rio;
import org.openrdf.rio.helpers.RDFHandlerBase;

import rdf.Dictionary;
import rdf.Index;

public final class RDFRawParser {
	
	private static Dictionary dictionary = new Dictionary();
	private static Index index = new Index();
	
	public static Dictionary getDictionary() {
        return dictionary;
    }

    public static Index getIndex() {
        return index;
    }

	static class RDFListener extends RDFHandlerBase {

		//ArrayList<String> words;
		
		@Override
		public void handleStatement(Statement st) {
			//words.add(st.getSubject());
			int sIndex = dictionary.initDico(st.getSubject().toString());
			int pIndex = dictionary.initDico(st.getPredicate().toString());
			int oIndex = dictionary.initDico(st.getObject().toString());
			index.addToIndex(sIndex, pIndex, oIndex);

			//System.out.println("\n" + st.getSubject() + "\t " + st.getPredicate() +"\t " + st.getObject());
		}
	
	}
	
	public static void readFile(File file) throws FileNotFoundException {
		
		System.out.println("Lecture du jeu de données " + file.getPath());
		Reader reader = new FileReader(file);

        org.openrdf.rio.RDFParser rdfParser = Rio.createParser(RDFFormat.RDFXML);
        rdfParser.setRDFHandler(new RDFListener());
        try {
            rdfParser.parse(reader, "");
        } catch (Exception e) {
        }
        try {
            reader.close();
        } catch (IOException e) {
        }
	}


}