package de.fotomarburg.lido2crm.converter;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import com.stegmannsystems.edp.eje.api.IFormatGenerator;


public class LIDO_CRM_Generator implements IFormatGenerator {

	@Override
	public void generateFormat(InputStream source, OutputStream target, Properties params) {		
		Lido2CrmConverter converter = new Lido2CrmConverter();
		try {
			converter.run(source, target);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}