package rdf;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class ExportCSV {

	static void exportTime(String fileString, long durationParse, long durationQuery, long durationSolve, long totalDuration) throws IOException {
		File file = new File(fileString);
        FileWriter fileWriter = new FileWriter(file.getPath() + "/" + "time.csv");
        fileWriter.append("Tâche; Temps (en ms)" + "\n");
        fileWriter.append("Parser fichier").append(";").append(String.valueOf(durationParse)).append("\n");
        fileWriter.append("Parser des requêtes").append(";").append(String.valueOf(durationQuery)).append("\n");
        fileWriter.append("Résolution").append(";").append(String.valueOf(durationSolve)).append("\n");
        fileWriter.append("Temps total d'execution").append(";").append(String.valueOf(totalDuration)).append("\n");
        fileWriter.close();
        System.out.println("Exportation des temps effectuée avec succès");
    }
	
	static void exportStats(String fileString, ArrayList<Query> queries, Dictionary dictionary) throws IOException {
		File file = new File(fileString);
        FileWriter fileWriter = new FileWriter(file.getPath() + "/" + "stats.csv");
        float selecTotal = 0;
        float selectSum = 0;
        fileWriter.append("Sujet ;Nombre de relation ;Nombre de relation \"Selectivity\" (en %)\n");
        for(Query q : queries){
            for(Query.Triplet triplet : q.getTriplet()){
            	fileWriter.append(triplet.toString()).append(";");
            	fileWriter.append(String.valueOf(triplet.getSelectivite()) + ";");

                float selectivite = ((float) triplet.getSelectivite() / dictionary.getDictionary().size()) * 100 ;
                selectSum += (float) triplet.getSelectivite();
                selecTotal += selectivite;
                fileWriter.append(String.valueOf(selectivite) + "\n");
            }
        }
        fileWriter.append(";").append(String.valueOf(selectSum)).append(";").append(String.valueOf(selecTotal));
        fileWriter.close();
        System.out.println("Exportation des statistiques effectuée avec succès");
    }

    static void exportResults(String fileString, ArrayList<Query> queries, Dictionary dictionary) throws IOException {
		File file = new File(fileString);
        FileWriter fileWriter = new FileWriter(file.getPath() + "/" + "result.csv");
        fileWriter.append("Requête ; Relation avec la requête" + "\n");
        for(Query query : queries){
        	fileWriter.append(query.toString()).append(';').append('\n');
            for (Integer relation : query.getResultQuery()){
            	fileWriter.append(";");
            	fileWriter.append(dictionary.getDictionary().get(relation)).append(';').append('\n');
            }
            fileWriter.append('\n');
        }
        fileWriter.close();
        System.out.println("Exportation des résultats effectuée avec succès");

    }
	
}
