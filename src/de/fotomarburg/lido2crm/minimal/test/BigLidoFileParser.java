package de.fotomarburg.lido2crm.minimal.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Date;


/**
 * Converts GeoNames RDF dump to RDF files. 
 * @author balandi
 *
 */
public class BigLidoFileParser {

	public void run() throws IOException {

		String textFile = "D:/FotoMarburg2DDB-20170404/FotoMarburg2DDB-20170404.xml";
		
//		String head= "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><rdf:RDF xmlns:cc=\"http://creativecommons.org/ns#\" xmlns:dcterms=\"http://purl.org/dc/terms/\" xmlns:foaf=\"http://xmlns.com/foaf/0.1/\" xmlns:gn=\"http://www.geonames.org/ontology#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:wgs84_pos=\"http://www.w3.org/2003/01/geo/wgs84_pos#\">";
//		String foot = "</rdf:RDF>";
		
		String head = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<lido:lidoWrap "
				+ "xmlns:xlink=\"http:////www.w3.org//1999//xlink\" "
				+ "xmlns:xs=\"http:////www.w3.org//2001//XMLSchema\" "
				+ "xmlns:xsi=\"http:////www.w3.org//2001//XMLSchema-instance\" "
				+ "xmlns:html=\"http:////www.w3.org//1999//xhtml\" "
				+ "xmlns:stx=\"http:////www.startext.de//stxmap\" "
				+ "xmlns:lido=\"http:////www.lido-schema.org\" "
				+ "xmlns:rdf=\"http:////www.w3.org//1999//02//22-rdf-syntax-ns#\" "
				+ "xmlns:skos=\"http:////www.w3.org//2004//02//skos//core#\" "
				+ "xsi:schemaLocation=\"http:////www.lido-schema.org http:////www.lido-schema.org//schema//v1.0//lido-v1.0.xsd\">";
		
		String foot = "</lido:lidoWrap>";
		
		// Open the file
		FileInputStream fstream = new FileInputStream(textFile);
		BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

		String strLine;

		//Read File Line By Line
		int counter = 0;
		int fileName= 1;
		String mainStr= "";
		
		while ((strLine = br.readLine()) != null)   {
			// Print the content on the console			
			if(strLine.contains("<lido:lido>") ){
				String rdfString = "<lido:lido>";

				while (!rdfString.contains("</lido:lido>")){
					rdfString = rdfString + br.readLine();
				} 
				
				// rdf cleanup remove head an foot
//				rdfString = rdfString.replace(head, "");
//				rdfString = rdfString.replace(foot,"");

				if(counter < 1000){			
					mainStr = mainStr + "\n" + rdfString;
					counter++;
				} else {
					storeStringAsRDFFile(mainStr,"D:/big lidos/", String.valueOf(fileName), head, foot );
					System.out.println(String.valueOf(fileName) + " stored.");;
					counter = 0;
					mainStr= "";
					fileName++;
				}
			}
		}
		
		storeStringAsRDFFile(mainStr,"D:/big lidos/", String.valueOf(fileName),  head, foot);
		System.out.println(String.valueOf(fileName) + " stored.");

		//Close the input stream
		br.close();	
		
		//Get end time
		Date date = new Date();
		System.out.println(date.toString());
		
	}
	
	public void storeStringAsRDFFile(String text, String path, String fileName, String head, String foot){
		
		// add head and foot
		text = head + "\n" + text + "\n" + foot;
		
		File target = new File(path + fileName + ".xml");
		
		try {
			Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(target), "UTF8"));

			out.append(text);
			out.flush();
			out.close();
		        
		    } 
		   catch (UnsupportedEncodingException e) 
		   {
			System.out.println(e.getMessage());
		   } 
		   catch (IOException e) 
		   {
			System.out.println(e.getMessage());
		    }
		   catch (Exception e)
		   {
			System.out.println(e.getMessage());
		   } 
	}
}