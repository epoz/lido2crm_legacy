package de.fotomarburg.lido2crm.minimal.test;

import java.io.IOException;

//import static org.junit.Assert.*;

import org.junit.Test;

import de.fotomarburg.lido2crm.converter.MainConverter;

public class BigTest {

	@Test
	public void test() throws IOException {		
//		BigLidoFileParser blp = new BigLidoFileParser();
//		blp.run();
		
		String source = "D:/big lidos/";
		String target = "D:/bl output/";
		MainConverter.run(source, target);
	}
}