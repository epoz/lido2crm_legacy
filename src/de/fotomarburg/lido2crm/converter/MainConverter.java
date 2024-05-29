package de.fotomarburg.lido2crm.converter;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class MainConverter {
	
	public static void run( String sourcePath, String targetPath ) throws IOException{
		
		LIDO_CRM_Generator crmlidoGenerator = new LIDO_CRM_Generator();
		
		try (Stream<Path> filePathStream= Files.walk(Paths.get(sourcePath))) {
		    filePathStream.forEach(filePath -> {
		        if (Files.isRegularFile(filePath)) {
		        	String targetFile = filePath.getFileName().toString().replaceAll("xml", "rdf");
		        	
		        	InputStream source = null;
					try {
						source = new FileInputStream(filePath.toString());
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
		        	OutputStream target = null;
					try {
						target = new FileOutputStream(targetPath + targetFile);
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		     
		        	crmlidoGenerator.generateFormat(source, target, null);
		        	System.out.println(filePath.getFileName().toString());
		        }
		    });
		}
	}
}