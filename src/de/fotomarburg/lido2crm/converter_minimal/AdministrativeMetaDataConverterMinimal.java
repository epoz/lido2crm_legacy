package de.fotomarburg.lido2crm.converter_minimal;

import java.util.List;

import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntModel;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.filter.Filters;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;

import de.fotomarburg.lido2crm.ontology.CIDOC_CRM;
import de.fotomarburg.lido2crm.ontology.Namespace_Constants;


/**
 * 
 * @author balandi
 *
 */
public class AdministrativeMetaDataConverterMinimal {

	public Document administrativeMetaData;
	public Namespace lidoNamespace;
	public XPathFactory xpathFactory;

	public AdministrativeMetaDataConverterMinimal(Element administrativeMetaData, Namespace lidoNamespace){
		this.administrativeMetaData = new Document();
		this.administrativeMetaData.addContent(administrativeMetaData.clone());
		this.lidoNamespace = lidoNamespace;
		this.xpathFactory = XPathFactory.instance();
	}

	public void addRightsWorkWrap(Individual mainObject, OntModel model, String lidoRecID){

		//rightsWorkWrap (0-1)
		//rightsWorkSet (0-n)
		String rightWorkSetPath = "/lido:administrativeMetadata/lido:rightsWorkWrap/lido:rightsWorkSet";
		XPathExpression<Element> expr = xpathFactory.compile( rightWorkSetPath, Filters.element(), null, lidoNamespace );
		List<Element> rightWorkSetContainer = expr.evaluate(administrativeMetaData);

		int mainCounter = 1;
		for(Element rightWorkSet:rightWorkSetContainer){

			//right object
			Individual rightInd = model.createIndividual(Namespace_Constants.BFM_NAMESPACE + lidoRecID + "_501_" + mainCounter, CIDOC_CRM.E30_Right);

			//rightsType (0-n)
			int counter = 1;
			for ( Element rightsType : rightWorkSet.getChildren("rightsType", lidoNamespace)){
				Individual rightsTypeInd = LidoElementHandlerMinimal.getType(rightsType, model, Namespace_Constants.BFM_NAMESPACE, "", lidoNamespace, lidoRecID,
						"502_" + String.valueOf(mainCounter) + "_" + String.valueOf(counter), CIDOC_CRM.E55_Type );
				rightInd.addProperty(CIDOC_CRM.P2_has_type, rightsTypeInd );
				counter++;
			}

			//rightsDate (0-1)
			if(rightWorkSet.getChild("rightsDate", lidoNamespace) != null){
				
				Individual timeSpan  =LidoElementHandlerMinimal.getTimeSpan( rightWorkSet.getChild("rightsDate", lidoNamespace),  model, Namespace_Constants.BFM_NAMESPACE, 
						"" ,  lidoNamespace,  lidoRecID, "503_" + String.valueOf(mainCounter) + "_" + String.valueOf(counter));

				rightInd.addProperty( CIDOC_CRM.P4_has_time_span, timeSpan );
			}

			//rightsHolder (0-n)
			counter = 1;
			for ( Element rightsHolder : rightWorkSet.getChildren("rightsHolder", lidoNamespace)){

				Individual legalBodyInd = model.createIndividual(Namespace_Constants.BFM_NAMESPACE + lidoRecID + "_504_" + mainCounter + "_" + counter, CIDOC_CRM.E39_Actor);

				//legal body id (0-n)
				for( Element legalBodyID : rightsHolder.getChildren("legalBodyID", lidoNamespace) )
					legalBodyInd.addProperty( CIDOC_CRM.P48_has_preferred_identifier, legalBodyID .getValue());

				//legal body name (0-n)
				for(Element legalBodyName : rightsHolder.getChildren("legalBodyName", lidoNamespace)){

					//appellation value (1-n) 
					for( Element appellationValue : legalBodyName.getChildren("appellationValue", lidoNamespace) )
						legalBodyInd.addProperty( CIDOC_CRM.P1_is_identified_by, appellationValue.getValue());

					//source appellation (0-n)
					for( Element sourceAppellation : legalBodyName.getChildren("sourceAppellation", lidoNamespace) )
						legalBodyInd.addProperty( CIDOC_CRM.P3_has_note, sourceAppellation.getValue() );

				}

				//legal body web link (0-n)
				for( Element legalBodyWeblink : rightsHolder.getChildren("legalBodyWeblink", lidoNamespace) )
					legalBodyInd.addProperty( CIDOC_CRM.P3_has_note, legalBodyWeblink.getValue() );

				//Connect to right
				rightInd.addProperty(CIDOC_CRM.P75i_is_possessed_by, legalBodyInd);
				counter++;
			}

			//credit line (0-n)
			for(Element creditLine : rightWorkSet.getChildren("creditLine", lidoNamespace))
				rightInd.addProperty( CIDOC_CRM.P3_has_note, creditLine.getValue() );

			//main object and right
			mainObject.addProperty(CIDOC_CRM.P104_is_subject_to, rightInd);
			mainCounter++;
		}
	}

	public void addRecordWrap(Individual mainObject, OntModel model, String lidoRecID ){

		//recordWrap(1)

		Element recordWrap = administrativeMetaData.getRootElement().getChild("recordWrap", lidoNamespace); 
		Individual recordInd = model.createIndividual(Namespace_Constants.BFM_NAMESPACE + lidoRecID + "_601", CIDOC_CRM.E31_Document);

		//recordID (1-n) / 602
		for( Element recordId : recordWrap.getChildren("recordID ", lidoNamespace) ){
		
			recordInd.addProperty(CIDOC_CRM.P48_has_preferred_identifier, recordId.getValue());
		}

		//recordType (1) ???
		if(recordWrap.getChild("recordType",lidoNamespace) != null){
			Individual recordTypeInd = LidoElementHandlerMinimal.getType(recordWrap.getChild("recordType", lidoNamespace), model, Namespace_Constants.BFM_NAMESPACE,
					"recordType/", lidoNamespace, lidoRecID + "_603", "", CIDOC_CRM.E55_Type  );
			recordInd.addProperty(CIDOC_CRM.P2_has_type , recordTypeInd);
		}

		//recordSource (1-n)
		int counter = 1;
		for(Element recordSource : recordWrap.getChildren("recordSource  ", lidoNamespace)){

			//object
			Individual legalBodyInd = model.createIndividual(Namespace_Constants.BFM_NAMESPACE + lidoRecID + "_604_" + counter, CIDOC_CRM.E39_Actor);

			//legal body id (0-n)
			for( Element legalBodyID : recordSource.getChildren("legalBodyID", lidoNamespace) )
				legalBodyInd.addProperty( CIDOC_CRM.P48_has_preferred_identifier, legalBodyID .getValue());

			//legal body name (0-n)
			for(Element legalBodyName : recordSource.getChildren("legalBodyName", lidoNamespace)){

				//appellation value (1-n) 
				for( Element appellationValue : legalBodyName.getChildren("appellationValue", lidoNamespace) )
					legalBodyInd.addProperty( CIDOC_CRM.P1_is_identified_by, appellationValue.getValue());

				//source appellation (0-n)
				for( Element sourceAppellation : legalBodyName.getChildren("sourceAppellation", lidoNamespace) )
					legalBodyInd.addProperty( CIDOC_CRM.P3_has_note, sourceAppellation.getValue() );

			}

			//legal body weblink (0-n)
			for( Element legalBodyWeblink : recordSource.getChildren("legalBodyWeblink", lidoNamespace) )
				legalBodyInd.addProperty( CIDOC_CRM.P3_has_note, legalBodyWeblink.getValue() );

			//creation record from legalbody
			//			Individual creation = model.createIndividual(Namespace_Constants.BFM_NAMESPACE + "recordCreation/" + lidoRecID, CIDOC_CRM.E65_Creation);
			//			creation.addProperty( CIDOC_CRM.P14_carried_out_by , legalBodyInd );
			//			creation.addProperty( CIDOC_CRM.P94_has_created , recordInd );
			
			recordInd.addProperty(CIDOC_CRM.P67_refers_to, legalBodyInd);
			
			counter++;

		}

		//recordRights (0-n)
		int mainCounter = 1;
		for(Element recordRights:recordWrap.getChildren("recordRights", lidoNamespace) ){

			//right object
			Individual rightInd = model.createIndividual(Namespace_Constants.BFM_NAMESPACE + lidoRecID + "_605_" + mainCounter, CIDOC_CRM.E30_Right);

			//rightsType (0-n)
			counter = 1;
			for ( Element rightsType : recordRights.getChildren("rightsType", lidoNamespace)){

				Individual rightsTypeInd = LidoElementHandlerMinimal.getType(rightsType, model, Namespace_Constants.BFM_NAMESPACE, 
						"", lidoNamespace, lidoRecID + "_606", String.valueOf(mainCounter) + "_" + String.valueOf(counter), CIDOC_CRM.E55_Type );
				
				rightInd.addProperty(CIDOC_CRM.P2_has_type, rightsTypeInd );
				counter++;
			}

			//rights date (0-1)
			if(recordRights.getChild("rightsDate ", lidoNamespace) != null){

				Element rightsDate = recordRights.getChild("subjectDate ", lidoNamespace);
				Individual timeSpan  = LidoElementHandlerMinimal.getTimeSpan( rightsDate,  model, Namespace_Constants.BFM_NAMESPACE, 
						"" ,  lidoNamespace,  lidoRecID + "_607", String.valueOf(mainCounter) + "_" + String.valueOf(counter));
				rightInd.addProperty( CIDOC_CRM.P4_has_time_span, timeSpan );

			}

			//rights holder (0-n)
			counter = 1;
			for ( Element rightsHolder : recordRights.getChildren("rightsHolder", lidoNamespace)){

				Individual legalBodyInd = model.createIndividual(Namespace_Constants.BFM_NAMESPACE
						+ lidoRecID + "_608_" + mainCounter  + "_" + counter, CIDOC_CRM.E39_Actor);

				//legal body id (0-n)
				for( Element legalBodyID : rightsHolder.getChildren("legalBodyID", lidoNamespace) )
					legalBodyInd.addProperty( CIDOC_CRM.P48_has_preferred_identifier, legalBodyID .getValue());

				//legal body name (0-n)
				for(Element legalBodyName : rightsHolder.getChildren("legalBodyName", lidoNamespace)){

					//appellation value (1-n) 
					for( Element appellationValue : legalBodyName.getChildren("appellationValue", lidoNamespace) )
						legalBodyInd.addProperty( CIDOC_CRM.P1_is_identified_by, appellationValue.getValue());

					//source appellation (0-n)
					for( Element sourceAppellation : legalBodyName.getChildren("sourceAppellation", lidoNamespace) )
						legalBodyInd.addProperty( CIDOC_CRM.P3_has_note, sourceAppellation.getValue() );
				}

				//legal body web link (0-n)
				for( Element legalBodyWeblink : rightsHolder.getChildren("legalBodyWeblink", lidoNamespace) )
					legalBodyInd.addProperty( CIDOC_CRM.P3_has_note, legalBodyWeblink.getValue() );

				//
				legalBodyInd.addProperty(CIDOC_CRM.P75_possesses, rightInd);
				counter++;
			}

			//credit line (0-n)
			for(Element creditLine : recordRights.getChildren("creditLine", lidoNamespace))
				rightInd.addProperty( CIDOC_CRM.P3_has_note, creditLine.getValue() );

			//main object and right
			recordInd.addProperty(CIDOC_CRM.P104_is_subject_to, rightInd);
			mainCounter++;

		}

		//recordInfoSet (0-n)
		counter = 1;
		for( Element recordInfoSet : recordWrap.getChildren("recordInfoSet", lidoNamespace)){

			Individual informationObjectInd = model.createIndividual(Namespace_Constants.BFM_NAMESPACE
					+ lidoRecID + "_609_" + counter, CIDOC_CRM.E73_Information_Object);

			//recordInfoID (0-n)
			for(Element recordInfoID : recordInfoSet.getChildren("recordInfoID"))
				informationObjectInd.addProperty(CIDOC_CRM.P48_has_preferred_identifier, recordInfoID.getValue());

			//recordInfoLink (0-n)
			for(Element recordInfoLink : recordInfoSet.getChildren("recordInfoLink"))
				informationObjectInd.addProperty(CIDOC_CRM.P3_has_note, recordInfoLink.getValue());

			//recordMetadataDate (0-n)
			for(Element recordMetadataDate : recordInfoSet.getChildren("recordMetadataDate"))
				informationObjectInd.addProperty(CIDOC_CRM.P1_is_identified_by, recordMetadataDate.getValue());

			//
			recordInd.addProperty(CIDOC_CRM.P129i_is_subject_of, informationObjectInd);
			counter++;

		}

		//connect to main object
		mainObject.addProperty(CIDOC_CRM.P70i_is_documented_in, recordInd ); 

	}

	public void addResourceWrap(Individual mainObject, OntModel model, String lidoRecID ){

		//resourceWrap (0-1)
		String resourceSetPath = "/lido:administrativeMetadata/lido:resourceWrap/lido:resourceSet";
		XPathExpression<Element> expr = xpathFactory.compile( resourceSetPath, Filters.element(), null, lidoNamespace );
		List<Element> resourceSetContainer = expr.evaluate(administrativeMetaData);

		//resource set (0-n)
		int mainCounter = 1;
		for(Element resourceSet: resourceSetContainer){

			Individual resourceInd = model.createIndividual(Namespace_Constants.BFM_NAMESPACE + lidoRecID + "_701_" + mainCounter, CIDOC_CRM.E73_Information_Object );

			//resource id (0-1) / 702
			if(resourceSet.getChild("resourceID", lidoNamespace) != null){
				resourceInd.addProperty(CIDOC_CRM.P48_has_preferred_identifier, resourceSet.getChild("resourceID", lidoNamespace).getValue());
			}

			//resource representation (0-n)
			//NICHT BETRACHTET - not used
			for(Element resourceRepresentation :  resourceSet.getChildren("resourceRepresentation", lidoNamespace) ){

				//link resource (1)
				resourceInd.addProperty( CIDOC_CRM.P3_has_note, resourceRepresentation.getChild( "linkResource", lidoNamespace).getValue());

				//resource measurements (0-n)
				int counter = 1;
				for( Element resourceMeasurementsSet : resourceRepresentation.getChildren( "resourceMeasurementsSet ", lidoNamespace )){

					Individual dimensionInd = model.createIndividual(Namespace_Constants.BFM_NAMESPACE + lidoRecID + "_703_" + mainCounter + "_" + counter, CIDOC_CRM.E54_Dimension );

					//Type (1-n)
					for( Element measurementType: resourceMeasurementsSet.getChildren("measurementType", lidoNamespace)){
						dimensionInd.addProperty(CIDOC_CRM.P2_has_type, measurementType.getValue());
					}

					//Unit (1-n)
					for(Element measurementUnit : resourceMeasurementsSet.getChildren("measurementUnit", lidoNamespace)){
						dimensionInd.addProperty(CIDOC_CRM.P91_has_unit, measurementUnit.getValue());
					}

					//Value (1)
					dimensionInd.addProperty(CIDOC_CRM.P90_has_value, resourceMeasurementsSet.getChild("measurementValue",lidoNamespace).getValue());

					//
					resourceInd.addProperty( CIDOC_CRM.P43_has_dimension, dimensionInd);
					counter++; 
				}
			}

			//resource type (0-1)
			if( resourceSet.getChild("resourceType", lidoNamespace) != null){
				Element resourceType = resourceSet.getChild("resourceType", lidoNamespace);
				
				Individual resourceTypeInd = LidoElementHandlerMinimal.getType(resourceType, model, Namespace_Constants.BFM_NAMESPACE,
						"", lidoNamespace, lidoRecID + "_704", "", CIDOC_CRM.E55_Type );
				
				resourceInd.addProperty(CIDOC_CRM.P2_has_type, resourceTypeInd);
			}

			//resource relation type (0-n)
			int counter = 1;
			for(Element resourceRelType : resourceSet.getChildren("resourceRelType", lidoNamespace) ){
				
				Individual resourceRelTypeInd = LidoElementHandlerMinimal.getType(resourceRelType, model, Namespace_Constants.BFM_NAMESPACE,
						"", lidoNamespace, lidoRecID + "_705", 
						String.valueOf(mainCounter) + "_" + String.valueOf(counter), CIDOC_CRM.E55_Type );
				
				resourceInd.addProperty(CIDOC_CRM.P2_has_type, resourceRelTypeInd);	
				counter++;
			}

			//resource perspective (0-n)
			counter = 1;
			for(Element resourcePerspective  : resourceSet.getChildren("resourcePerspective", lidoNamespace) ){
				
				Individual resourcePerspectiveInd = LidoElementHandlerMinimal.getType(resourcePerspective, model, Namespace_Constants.BFM_NAMESPACE,
						"", lidoNamespace, lidoRecID + "_706", String.valueOf(mainCounter) + "_" + String.valueOf(counter), CIDOC_CRM.E55_Type  );
				
				resourceInd.addProperty(CIDOC_CRM.P2_has_type, resourcePerspectiveInd);
				counter++;
			}

			//resource description (0-n)
			for(Element resourceDescription : resourceSet.getChildren("resourceDescription", lidoNamespace)){
				resourceInd.addProperty(CIDOC_CRM.P3_has_note, resourceDescription.getValue());
			}

			//resource date taken (0-1)
			if( resourceSet.getChild( "resourceDateTaken ", lidoNamespace ) != null ){

				Element resourceDateTaken = resourceSet.getChild("resourceDateTaken ", lidoNamespace);

				//displayDate (0-n)

				//date (0-1)
				if( resourceDateTaken.getChild( "date", lidoNamespace ) != null ){
					Individual timeSpan  = LidoElementHandlerMinimal.getTimeSpan(  resourceDateTaken.getChild( "date", lidoNamespace ),  model, Namespace_Constants.BFM_NAMESPACE, 
							"",  lidoNamespace,  lidoRecID + "_707", String.valueOf(mainCounter));
					resourceInd.addProperty( CIDOC_CRM.P4_has_time_span, timeSpan );
				}
			}

			//resource source (0-n)
			counter = 1;
			for(Element resourceSource : resourceSet.getChildren("resourceSource", lidoNamespace)){

				Individual legalBodyInd = model.createIndividual(Namespace_Constants.BFM_NAMESPACE + lidoRecID + "_708_" + mainCounter + "_" + counter, CIDOC_CRM.E39_Actor);

				//legal body id (0-n)
				for( Element legalBodyID : resourceSource.getChildren("legalBodyID", lidoNamespace) )
					legalBodyInd.addProperty( CIDOC_CRM.P48_has_preferred_identifier, legalBodyID .getValue());

				//legal body name (0-n)
				for(Element legalBodyName : resourceSource.getChildren("legalBodyName", lidoNamespace)){

					//appellation value (1-n) 
					for( Element appellationValue : legalBodyName.getChildren("appellationValue", lidoNamespace) )
						legalBodyInd.addProperty( CIDOC_CRM.P1_is_identified_by, appellationValue.getValue());

					//source appellation (0-n)
					for( Element sourceAppellation : legalBodyName.getChildren("sourceAppellation", lidoNamespace) )
						legalBodyInd.addProperty( CIDOC_CRM.P3_has_note, sourceAppellation.getValue() );

				}

				//legal body weblink (0-n)
				for( Element legalBodyWeblink : resourceSource.getChildren("legalBodyWeblink", lidoNamespace) )
					legalBodyInd.addProperty( CIDOC_CRM.P3_has_note, legalBodyWeblink.getValue() );

				//Link to lagal body
				resourceInd.addProperty(CIDOC_CRM.P67_refers_to, legalBodyInd);
				counter++;

			}

			//rights resource (0-n)
			int rightCounter = 1;

			for(Element rightsResource : resourceSet.getChildren("rightsResource ", lidoNamespace) ){

				//right object
				Individual rightInd = model.createIndividual(Namespace_Constants.BFM_NAMESPACE + lidoRecID + "_709_" + mainCounter + "_" + rightCounter, CIDOC_CRM.E30_Right);

				//rights type (0-n)
				counter = 1;
				for ( Element rightsType : rightsResource.getChildren("rightsType", lidoNamespace)){


					Individual rightsTypeInd = LidoElementHandlerMinimal.getType(rightsType, model, Namespace_Constants.BFM_NAMESPACE, "", lidoNamespace, lidoRecID + "_710",
							String.valueOf(mainCounter) + "_" + String.valueOf(rightCounter) + "_" + String.valueOf(counter), CIDOC_CRM.E55_Type );

					rightInd.addProperty(CIDOC_CRM.P2_has_type, rightsTypeInd);

					counter++;

				}

				//rights date (0-1)
				if(rightsResource.getChild("rightsDate ", lidoNamespace) != null){

					Element rightsDate = rightsResource.getChild("rightsDate ", lidoNamespace);

					Individual timeSpan  = LidoElementHandlerMinimal.getTimeSpan(  rightsDate,  model, Namespace_Constants.BFM_NAMESPACE, 
							"" ,  lidoNamespace,  lidoRecID + "_711", String.valueOf(mainCounter) + "_" + String.valueOf(rightCounter) + "_" + String.valueOf(counter) );
					rightInd.addProperty( CIDOC_CRM.P4_has_time_span, timeSpan );

				}

				//rights holder (0-n)
				counter = 1;

				for ( Element rightsHolder : rightsResource.getChildren("rightsHolder", lidoNamespace)){

					Individual legalBodyInd = model.createIndividual(Namespace_Constants.BFM_NAMESPACE + "" + lidoRecID + "_712_" + mainCounter + "_" + rightCounter + "_" + counter, CIDOC_CRM.E39_Actor);

					//legal body id (0-n)
					for( Element legalBodyID : rightsHolder.getChildren("legalBodyID", lidoNamespace) )
						legalBodyInd.addProperty( CIDOC_CRM.P48_has_preferred_identifier, legalBodyID .getValue());

					//legal body name (0-n)
					for(Element legalBodyName : rightsHolder.getChildren("legalBodyName", lidoNamespace)){

						//appellation value (1-n) 
						for( Element appellationValue : legalBodyName.getChildren("appellationValue", lidoNamespace) )
							legalBodyInd.addProperty( CIDOC_CRM.P1_is_identified_by, appellationValue.getValue());

						//source appellation (0-n)
						for( Element sourceAppellation : legalBodyName.getChildren("sourceAppellation", lidoNamespace) )
							legalBodyInd.addProperty( CIDOC_CRM.P3_has_note, sourceAppellation.getValue() );

					}

					//legal body web link (0-n)
					for( Element legalBodyWeblink : rightsHolder.getChildren("legalBodyWeblink", lidoNamespace) )
						legalBodyInd.addProperty( CIDOC_CRM.P3_has_note, legalBodyWeblink.getValue() );

					//Connect to right
					legalBodyInd.addProperty(CIDOC_CRM.P75_possesses, rightInd);
					counter++;
				}

				//credit line (0-n)
				for(Element creditLine : rightsResource.getChildren("creditLine", lidoNamespace))
					rightInd.addProperty( CIDOC_CRM.P3_has_note, creditLine.getValue() );

				//main object and right
				resourceInd.addProperty(CIDOC_CRM.P104_is_subject_to, rightInd);
				mainCounter++;

			}

			//connect to main object
			mainObject.addProperty(CIDOC_CRM.P129i_is_subject_of, resourceInd);
			mainCounter++;
		}

	}

}