package de.fotomarburg.lido2crm.test;

import static org.junit.Assert.*;

import org.junit.Test;

import de.fotomarburg.lido2crm.converter.LidoElementHandler;

public class Sparl_Endpoint_Test {

	@Test
	public void test() {
		
	String n = LidoElementHandler.getCRMEventTypeOfLidoEventType("http://terminology.lido-schema.org/lido00009");
	
	System.out.println(n);
	
		fail("Not yet implemented");
	}

}
