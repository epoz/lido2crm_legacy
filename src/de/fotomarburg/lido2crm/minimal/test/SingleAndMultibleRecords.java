package de.fotomarburg.lido2crm.minimal.test;

import java.io.IOException;

import org.junit.Test;

import de.fotomarburg.lido2crm.converter.MainConverter;

public class SingleAndMultibleRecords {

	@Test
	public void test() throws IOException {

//		String sourceSingle = "J:/- Projekte/Fotoerbe/CRM_RDF-Konvertierung/LIDO2CRM-APS/Testdata/lido_lido(single-record)/Input/";
		String sourceMultible = "J:/- Projekte/Fotoerbe/CRM_RDF-Konvertierung/LIDO2CRM-APS/Testdata/lido_lidoWrap(multiple-records)/Input/";
		
//		String targetSingle = "J:/- Projekte/Fotoerbe/CRM_RDF-Konvertierung/LIDO2CRM-APS/Testdata/lido_lido(single-record)/Output/";
		String targetMultible = "J:/- Projekte/Fotoerbe/CRM_RDF-Konvertierung/LIDO2CRM-APS/Testdata/lido_lidoWrap(multiple-records)/Output/";
			
//		MainConverter.run(sourceSingle, targetSingle);
		
		MainConverter.run(sourceMultible, targetMultible);

	}	
}
