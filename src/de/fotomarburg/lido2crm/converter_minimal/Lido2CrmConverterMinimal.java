package de.fotomarburg.lido2crm.converter_minimal;


import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.filter.Filters;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;

import de.fotomarburg.lido2crm.ontology.CIDOC_CRM;
import de.fotomarburg.lido2crm.ontology.CIDOC_CRM_PC;
import de.fotomarburg.lido2crm.ontology.Namespace_Constants;
import de.fotomarburg.lido2crm.xmlHandler.XMLHandler;


/**
 * 
 * @author balandi
 *
 */
public class Lido2CrmConverterMinimal {

	public int counter = 1;

	public String lidoRecIDGen = null;

	public void run(String sourceFile, String targetFile) throws FileNotFoundException{

		//model
		OntModel model = getModel();

		//read document
		Document document = XMLHandler.readXML(sourceFile);

		//document namespace
		Namespace ns = document.getRootElement().getNamespace();

		//root settings
		Element root = document.getRootElement();
		List<Element> lidos = null;

		if( root.getName().equals("lido")){
			lidos = new ArrayList<Element>();
			lidos.add(root);
		}else{
			lidos = root.getChildren("lido", ns); //LIDOs from Wrap 
		}

		
//		Blank Nodes Test
//		Resource blank1 = model.createResource(new AnonId("A33"));
//		Resource blank2 = model.createResource(new AnonId("2"));
//		blank1.addLiteral(CIDOC_CRM.P3_has_note, "blank node 1");
//		blank2.addLiteral(CIDOC_CRM.P3_has_note, "blank node 2");
		
		for(Element lido : lidos){

			Individual mainObject = createMainObject(lido, model, ns );
			
//			System.out.println(blank1.getId().getBlankNodeId());
//			mainObject.addProperty(CIDOC_CRM.P167_was_at, blank1);
//			mainObject.addProperty(CIDOC_CRM.P167_was_at, blank2);
			
			if (mainObject != null){

				//call sub functions 

				//descriptive meta data
				DescriptiveMetaDataConverterMinimal descriptiveMetadataConverter = new DescriptiveMetaDataConverterMinimal( lido.getChild("descriptiveMetadata",ns), ns );

				//1. object classification
				descriptiveMetadataConverter.addObjectClassificationWrap(mainObject, model, lidoRecIDGen);

				//2. object identification
				descriptiveMetadataConverter.addObjectIdentificationWrap(mainObject, model, lidoRecIDGen);

				//3. event wrap
				descriptiveMetadataConverter.addEventWrap(mainObject, model, lidoRecIDGen);

				//4. object relation wrap
				descriptiveMetadataConverter.addObjectRelationWrap(mainObject, model, lidoRecIDGen);

				//administrative meta data
				AdministrativeMetaDataConverterMinimal administrativMetadataConverter = new AdministrativeMetaDataConverterMinimal( lido.getChild("administrativeMetadata",ns), ns );

				//1. right work wrap
				administrativMetadataConverter.addRightsWorkWrap(mainObject, model, lidoRecIDGen);

				//2. record wrap
				administrativMetadataConverter.addRecordWrap(mainObject, model, lidoRecIDGen);

				//3. resource wrap
				administrativMetadataConverter.addResourceWrap(mainObject, model, lidoRecIDGen);

			}
		}

		//output sequence
		Resource[] res = new Resource[6];

		res[0] = CIDOC_CRM.E18_Physical_Thing;
		res[1] = CIDOC_CRM.E53_Place;
		res[2] = CIDOC_CRM.E7_Activity;
		res[3] = CIDOC_CRM.E39_Actor;
		res[4] = CIDOC_CRM.E31_Document;
		res[5] = CIDOC_CRM_PC.PC14_carried_out_by;

		XMLHandler.writeRDF(model, targetFile, res);

	}

	private Individual createMainObject(Element lido, OntModel model, Namespace ns){

		Document lidoSingleDocument = new Document();
		lidoSingleDocument.addContent(lido.clone());
		XPathFactory xpathFactory = XPathFactory.instance();
		XPathExpression<Element> expr = null;

		//preferred lidoRecID (1)
		String prefferedLidoRecIDPath = "/lido:lido/lido:lidoRecID";  //[@lido:pref='preferred']";
		expr = xpathFactory.compile( prefferedLidoRecIDPath, Filters.element(), null, ns );
		Element prefferedLidoRecID = expr.evaluateFirst(lidoSingleDocument);

		if(prefferedLidoRecID == null){
			//ERROR LOG
			System.out.println("There is no preferred LidoRecID.");

			return null;

		} else {

			String prefferedLidoRecIDValue = normalizeID(prefferedLidoRecID.getValue());
			lidoRecIDGen = prefferedLidoRecIDValue;
			
			// LIDO main object
			Individual lidoObject = model.createIndividual( Namespace_Constants.BFM_NAMESPACE + prefferedLidoRecIDValue,  CIDOC_CRM.E18_Physical_Thing);	

			//lidoRecID (1-n) - (1 in DDK&BFM )
			List<Element> lidoRecIDContainer = lidoSingleDocument.getRootElement().getChildren("lidoRecID", ns);
			
			int counter = 1;
			for(Element lidoRecID:lidoRecIDContainer){
				
				Individual identifierLidoRec = model.createIndividual( Namespace_Constants.BFM_NAMESPACE + prefferedLidoRecIDValue + "_001_" + counter,  CIDOC_CRM.E42_Identifier );
				identifierLidoRec.addProperty(CIDOC_CRM.P3_has_note, lidoRecID.getValue());
				
				String preferred = lidoRecID.getAttributeValue("pref",ns);
				
				if(preferred == null)
					lidoObject.addProperty(CIDOC_CRM.P1_is_identified_by, identifierLidoRec);
				else
					lidoObject.addProperty(CIDOC_CRM.P48_has_preferred_identifier, identifierLidoRec);
					
				counter++;
			}				

//			//object published ID (0-n)
//			String objectPublishedIDPath = "/lido:lido/lido:objectPublishedID[@lido:type='http://terminology.lido-schema.org/lido00100']";
//			expr = xpathFactory.compile( objectPublishedIDPath, Filters.element(), null, ns );
//			Element objectPublishedID = expr.evaluateFirst(lidoSingleDocument);
//
//			//LIDO object (main object)
//			if(objectPublishedID != null){
//
//				String pubID = objectPublishedID.getValue().replace("DE-Mb112/", "").replaceAll(",", "_");
////				Individual lidoObject = model.createIndividual( Namespace_Constants.BFM_NAMESPACE + pubID,  CIDOC_CRM.m_model.getOntClass(categoryConceptID));				
//
//				//lidoRecID & objectPublishedID
//				lidoDocument.addProperty(CIDOC_CRM.P70_documents, lidoObject);
//
//				//other objectPublishedIDs, match:starts-with(@source,'http') 
//				String objectPublishedIDContainerPath = "/lido:lido/lido:objectPublishedID[@lido:type='http://terminology.lido-schema.org/lido00099']";
//				expr = xpathFactory.compile( objectPublishedIDContainerPath, Filters.element(), null, ns );
//				List<Element> objectPublishedIDContainer = expr.evaluate(lidoSingleDocument);
//
//				for(Element sameAsObjectPublishedID : objectPublishedIDContainer){
//					lidoObject.addProperty(OWL.sameAs, sameAsObjectPublishedID.getValue());
//				}
//			} 
			
			return lidoObject;
		}
	}

	private OntModel getModel(){

		//model	
		OntModel MODEL = ModelFactory.createOntologyModel( OntModelSpec.RDFS_MEM );

		//Add Namespace
		MODEL.setNsPrefix( "fmc", "http://www.fotomarburg.de/content/" );
		MODEL.setNsPrefix( "crm", CIDOC_CRM.NS );
		MODEL.setNsPrefix( "crm_pc", CIDOC_CRM_PC.NS );
		MODEL.setNsPrefix( "skos", "http://www.w3.org/2004/02/skos/core#" );

		return MODEL;

	}
	
	private String normalizeID(String id ){
		
		String[] splittedId = id.split("/");
		
		if(splittedId.length >0 ){
			id = splittedId[splittedId.length-1];
		}
		
		id.replaceAll("\\,", "_");

		return id;
	}

}