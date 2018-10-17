import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.openrdf.model.Statement;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.Rio;
import org.openrdf.rio.helpers.RDFHandlerBase;

public final class RDFRawParser {

	private static class RDFListener extends RDFHandlerBase {

		//Collection<String> words;
		
		@Override
		public void handleStatement(Statement st) {
			//words.add(st.getSubject());
			System.out.println("\n" + st.getSubject() + "\t " + st.getPredicate() +"\t " + st.getObject());
		}
	};
		
	
	public static void main(String args[]) throws FileNotFoundException {

		Reader reader = new FileReader("src/fichier/100K.rdfxml");

		org.openrdf.rio.RDFParser rdfParser = Rio
				.createParser(RDFFormat.RDFXML);
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