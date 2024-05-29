package de.fotomarburg.lido2crm.minimal.test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.junit.Test;

import de.fotomarburg.lido2crm.converter_minimal.Lido2CrmConverterMinimal;


/**
 * 
 * @author balandi
 *
 */
public class RDF_Converter_Test_Minimal {

	@Test
	public void test() throws IOException {
		
//		String source = "C:/Users/balandi/workspace/lido2crm/testInputFiles/";
//		String target = "C:/Users/balandi/workspace/lido2crm/testOutputFiles/";

		String source = "DQCInput/";
		String target = "DQCOutput/";
		
		this.run(source, target);
	
	}	

	public void run( String sourcePath, String targetPath ) throws IOException{

		Lido2CrmConverterMinimal converter = new Lido2CrmConverterMinimal();

		try (Stream<Path> filePathStream= Files.walk(Paths.get(sourcePath))) {
			filePathStream.forEach(filePath -> {
				if (Files.isRegularFile(filePath)) {

					System.out.println(filePath.getFileName().toString());
					
					String targetFile = filePath.getFileName().toString().replaceAll("xml", "rdf");
					String source = filePath.toString();
					String target = targetPath + targetFile;
					try {
						converter.run(source, target);
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			});
		}
	}
}