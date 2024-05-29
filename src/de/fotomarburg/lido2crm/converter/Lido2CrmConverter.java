package de.fotomarburg.lido2crm.converter;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.OWL;
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
public class Lido2CrmConverter {

	public int counter = 1;

	public String lidoRecIDGen = null;

	public void run(InputStream string, OutputStream string2) throws FileNotFoundException{

		//model
		OntModel model = getModel();

		//read document
		Document document = XMLHandler.readXML(string);

		//document namespace
		Namespace ns = document.getRootElement().getNamespace();

		//root
		Element root = document.getRootElement();

		List<Element> lidos = null;

		if( root.getName().equals("lido")){
			lidos = new ArrayList<Element>();
			lidos.add(root);
		}else{
			lidos = root.getChildren("lido", ns); //LIDOs from Wrap 
		}

		for(Element lido : lidos){

			Individual mainObject = createMainObject(lido, model, ns );

			//call sub functions 

			//descriptive meta data
			DescriptiveMetadataConverter descriptiveMetadataConverter = new DescriptiveMetadataConverter( lido.getChild("descriptiveMetadata",ns), ns );

			//1. object classification
			descriptiveMetadataConverter.addObjectClassificationWrap(mainObject, model, lidoRecIDGen);

			//2. object identification
			descriptiveMetadataConverter.addObjectIdentificationWrap(mainObject, model, lidoRecIDGen);

			//3. event wrap
			descriptiveMetadataConverter.addEventWrap(mainObject, model, lidoRecIDGen);

			//4. object relation wrap
			descriptiveMetadataConverter.addObjectRelationWrap(mainObject, model, lidoRecIDGen);

			//administrative meta data
			AdministrativeMetaDataConverter administrativMetadataConverter = new AdministrativeMetaDataConverter( lido.getChild("administrativeMetadata",ns), ns );

			//1. right work wrap
			administrativMetadataConverter.addRightsWorkWrap(mainObject, model, lidoRecIDGen);

			//2. record wrap
			administrativMetadataConverter.addRecordWrap(mainObject, model, lidoRecIDGen);

			//3. resource wrap
			administrativMetadataConverter.addResourceWrap(mainObject, model, lidoRecIDGen);

		}

		//output
		Resource[] res = new Resource[6];

		res[0] = CIDOC_CRM.E22_Man_Made_Object;
		res[1] = CIDOC_CRM.E53_Place;
		res[2] = CIDOC_CRM.E7_Activity;
		res[3] = CIDOC_CRM.E39_Actor;
		res[4] = CIDOC_CRM.E31_Document;
		res[5] = CIDOC_CRM_PC.PC14_carried_out_by;

		XMLHandler.writeRDF(model, string2, res);

	}
	
	public void run(String string, String string2) throws FileNotFoundException{

		//model
		OntModel model = getModel();

		//read document
		Document document = XMLHandler.readXML(string);

		//document namespace
		Namespace ns = document.getRootElement().getNamespace();

		//root
		Element root = document.getRootElement();

		List<Element> lidos = null;

		if( root.getName().equals("lido")){
			lidos = new ArrayList<Element>();
			lidos.add(root);
		}else{
			lidos = root.getChildren("lido", ns); //LIDOs from Wrap 
		}

		for(Element lido : lidos){

			Individual mainObject = createMainObject(lido, model, ns );

			//call sub functions 

			//descriptive meta data
			DescriptiveMetadataConverter descriptiveMetadataConverter = new DescriptiveMetadataConverter( lido.getChild("descriptiveMetadata",ns), ns );

			//1. object classification
			descriptiveMetadataConverter.addObjectClassificationWrap(mainObject, model, lidoRecIDGen);

			//2. object identification
			descriptiveMetadataConverter.addObjectIdentificationWrap(mainObject, model, lidoRecIDGen);

			//3. event wrap
			descriptiveMetadataConverter.addEventWrap(mainObject, model, lidoRecIDGen);

			//4. object relation wrap
			descriptiveMetadataConverter.addObjectRelationWrap(mainObject, model, lidoRecIDGen);

			//administrative meta data
			AdministrativeMetaDataConverter administrativMetadataConverter = new AdministrativeMetaDataConverter( lido.getChild("administrativeMetadata",ns), ns );

			//1. right work wrap
			administrativMetadataConverter.addRightsWorkWrap(mainObject, model, lidoRecIDGen);

			//2. record wrap
			administrativMetadataConverter.addRecordWrap(mainObject, model, lidoRecIDGen);

			//3. resource wrap
			administrativMetadataConverter.addResourceWrap(mainObject, model, lidoRecIDGen);

		}

		//output
		Resource[] res = new Resource[6];

		res[0] = CIDOC_CRM.E22_Man_Made_Object;
		res[1] = CIDOC_CRM.E53_Place;
		res[2] = CIDOC_CRM.E7_Activity;
		res[3] = CIDOC_CRM.E39_Actor;
		res[4] = CIDOC_CRM.E31_Document;
		res[5] = CIDOC_CRM_PC.PC14_carried_out_by;

		XMLHandler.writeRDF(model, string2, res);

	}

	private Individual createMainObject(Element lido, OntModel model, Namespace ns){

		Document lidoSingleDocument = new Document();
		lidoSingleDocument.addContent(lido.clone());
		XPathFactory xpathFactory = XPathFactory.instance();
		XPathExpression<Element> expr =null;

		String categoryConceptID = null;

		//category (0-1)
		if( lidoSingleDocument.getRootElement().getChild("category", ns) != null ){
			categoryConceptID = lidoSingleDocument.getRootElement().getChild("category", ns).getChild("conceptID", ns).getValue();
		}

		//lidoRecID (1-n) - (1 in DDK&BFM )
		String lidoRecID = lidoSingleDocument.getRootElement().getChild("lidoRecID", ns).getValue();
		Individual lidoDocument = model.createIndividual( Namespace_Constants.BFM_NAMESPACE + lidoRecID.replace("DE-Mb112/", ""),  CIDOC_CRM.E31_Document );				

		//lidoRecIDGen
		String[] ids = lidoRecID.split("/");
		String id = ids[ids.length-1];
		this.lidoRecIDGen = id.replaceAll(",", "_");

		//object published ID (0-n)
		String objectPublishedIDPath = "/lido:lido/lido:objectPublishedID[@lido:type='http://terminology.lido-schema.org/lido00100']";
		expr = xpathFactory.compile( objectPublishedIDPath, Filters.element(), null, ns );
		Element objectPublishedID = expr.evaluateFirst(lidoSingleDocument);

		//LIDO object (main object)
		if(objectPublishedID != null){
			String pubID = objectPublishedID.getValue().replace("DE-Mb112/", "").replaceAll(",", "_");
			Individual lidoObject = model.createIndividual( Namespace_Constants.BFM_NAMESPACE + pubID,  CIDOC_CRM.m_model.getOntClass(categoryConceptID));				

			//lidoRecID & objectPublishedID
			lidoDocument.addProperty(CIDOC_CRM.P70_documents, lidoObject);

			//other objectPublishedIDs, match:starts-with(@source,'http') 
			String objectPublishedIDContainerPath = "/lido:lido/lido:objectPublishedID[@lido:type='http://terminology.lido-schema.org/lido00099']";
			expr = xpathFactory.compile( objectPublishedIDContainerPath, Filters.element(), null, ns );
			List<Element> objectPublishedIDContainer = expr.evaluate(lidoSingleDocument);

			for(Element sameAsObjectPublishedID : objectPublishedIDContainer){
				lidoObject.addProperty(OWL.sameAs, sameAsObjectPublishedID.getValue());
			}

			return lidoObject;
		} else {
			
			Individual lidoObject = model.createIndividual(Namespace_Constants.BFM_NAMESPACE + lidoRecID.replace("DE-Mb112/object/", ""),  CIDOC_CRM.m_model.getOntClass(categoryConceptID));				

			//lidoRecID & objectPublishedID
			lidoDocument.addProperty(CIDOC_CRM.P70_documents, lidoObject);
			return lidoObject;
		}

	}

	private  OntModel getModel(){

		//model	
		OntModel MODEL = ModelFactory.createOntologyModel( OntModelSpec.RDFS_MEM );

		//Add Namespace
		MODEL.setNsPrefix("fmc", "http://www.fotomarburg.de/content/" );
		MODEL.setNsPrefix("crm", CIDOC_CRM.NS );
		MODEL.setNsPrefix("crm_pc", CIDOC_CRM_PC.NS );
		MODEL.setNsPrefix("skos", "http://www.w3.org/2004/02/skos/core#" );

		return MODEL;

	}

}