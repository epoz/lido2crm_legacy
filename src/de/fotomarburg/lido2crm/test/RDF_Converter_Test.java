package de.fotomarburg.lido2crm.test;

import java.io.IOException;

import org.junit.Test;

import de.fotomarburg.lido2crm.converter.MainConverter;


/**
 * 
 * @author balandi
 *
 */
public class RDF_Converter_Test {

	@Test
	public void test() throws IOException {

		String source = "testInputFiles";
		String target = "testOutputFiles/";
		MainConverter.run(source, target);

	}	
	
	
}