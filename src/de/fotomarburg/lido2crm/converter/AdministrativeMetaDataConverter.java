package de.fotomarburg.lido2crm.converter;

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
public class AdministrativeMetaDataConverter {

	public Document administrativeMetaData;
	public Namespace lidoNamespace;
	public XPathFactory xpathFactory;

	public AdministrativeMetaDataConverter(Element administrativeMetaData, Namespace lidoNamespace){
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
			Individual rightInd = model.createIndividual(Namespace_Constants.BFM_NAMESPACE + "right/" + lidoRecID + "_" + mainCounter, CIDOC_CRM.E30_Right);

			//rightsType (0-n)
			int counter = 1;
			for ( Element rightsType : rightWorkSet.getChildren("rightsType", lidoNamespace)){
				Individual rightsTypeInd = LidoElementHandler.getType(rightsType, model, Namespace_Constants.BFM_NAMESPACE, "rightsType/", lidoNamespace, lidoRecID,
						String.valueOf(mainCounter) + "_" + String.valueOf(counter), CIDOC_CRM.E55_Type );
				rightInd.addProperty(CIDOC_CRM.P2_has_type, rightsTypeInd );
				counter++;
			}

			//rightsDate (0-1)
			if(rightWorkSet.getChild("rightsDate", lidoNamespace) != null){
				
				Individual timeSpan  = model.createIndividual(Namespace_Constants.BFM_NAMESPACE + "rightsDate/" + lidoRecID + "_" + mainCounter + "_" + counter, CIDOC_CRM.E52_Time_Span); //later specific type 
				LidoElementHandler.getTimeSpan( rightWorkSet.getChild("subjectDate ", lidoNamespace),  model, Namespace_Constants.BFM_NAMESPACE, 
						"rightsDate/" ,  lidoNamespace,  lidoRecID, String.valueOf(mainCounter) + "_" + String.valueOf(counter));

				rightInd.addProperty( CIDOC_CRM.P4_has_time_span, timeSpan );
			}

			//rightsHolder (0-n)
			counter = 1;
			for ( Element rightsHolder : rightWorkSet.getChildren("rightsHolder", lidoNamespace)){

				Individual legalBodyInd = model.createIndividual(Namespace_Constants.BFM_NAMESPACE + "rightsHolderRight/" + lidoRecID + "_" + mainCounter + "_" + counter, CIDOC_CRM.E39_Actor);

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
		Individual recordInd = model.createIndividual(Namespace_Constants.BFM_NAMESPACE + "record/" + lidoRecID, CIDOC_CRM.E31_Document);

		//recordID (1-n)
		for( Element recordId : recordWrap.getChildren("recordID ", lidoNamespace) )
			recordInd.addProperty(CIDOC_CRM.P48_has_preferred_identifier, recordId.getValue());

		//recordType (1) ???
		if(recordWrap.getChild("recordType",lidoNamespace) != null){
			Individual recordTypeInd = LidoElementHandler.getType(recordWrap.getChild("recordType", lidoNamespace), model, Namespace_Constants.BFM_NAMESPACE, "recordType/", lidoNamespace, lidoRecID, "", CIDOC_CRM.E55_Type  );
			recordInd.addProperty(CIDOC_CRM.P2_has_type , recordTypeInd);
		}

		//recordSource (1-n)
		int counter = 1;
		for(Element recordSource : recordWrap.getChildren("recordSource  ", lidoNamespace)){

			//object
			Individual legalBodyInd = model.createIndividual(Namespace_Constants.BFM_NAMESPACE + "recordSource/" + lidoRecID + "_" + counter, CIDOC_CRM.E39_Actor);

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
			Individual creation = model.createIndividual(Namespace_Constants.BFM_NAMESPACE + "recordCreation/" + lidoRecID, CIDOC_CRM.E65_Creation);
			creation.addProperty( CIDOC_CRM.P14_carried_out_by , legalBodyInd );
			creation.addProperty( CIDOC_CRM.P94_has_created , recordInd );
			counter++;

		}

		//recordRights (0-n)
		int mainCounter = 1;
		for(Element recordRights:recordWrap.getChildren("recordRights", lidoNamespace) ){

			//right object
			Individual rightInd = model.createIndividual(Namespace_Constants.BFM_NAMESPACE + "recordRights/" + lidoRecID + "_" + mainCounter, CIDOC_CRM.E30_Right);

			//rightsType (0-n)
			counter = 1;
			for ( Element rightsType : recordRights.getChildren("rightsType", lidoNamespace)){

				Individual rightsTypeInd = LidoElementHandler.getType(rightsType, model, Namespace_Constants.BFM_NAMESPACE, "recordRightsType/", lidoNamespace, lidoRecID, String.valueOf(mainCounter) + "_" + String.valueOf(counter), CIDOC_CRM.E55_Type );
				rightInd.addProperty(CIDOC_CRM.P2_has_type, rightsTypeInd );
				counter++;
			}

			//rights date (0-1)
			if(recordRights.getChild("rightsDate ", lidoNamespace) != null){

				Element rightsDate = recordRights.getChild("subjectDate ", lidoNamespace);
				Individual timeSpan  = LidoElementHandler.getTimeSpan( rightsDate,  model, Namespace_Constants.BFM_NAMESPACE, "recordRightsDate/" ,  lidoNamespace,  lidoRecID, String.valueOf(mainCounter) + "_" + String.valueOf(counter));
				rightInd.addProperty( CIDOC_CRM.P4_has_time_span, timeSpan );

			}

			//rights holder (0-n)
			counter = 1;
			for ( Element rightsHolder : recordRights.getChildren("rightsHolder", lidoNamespace)){

				Individual legalBodyInd = model.createIndividual(Namespace_Constants.BFM_NAMESPACE + "recordRightsHolder/" + lidoRecID + "_" + mainCounter  + "_" + counter, CIDOC_CRM.E39_Actor);

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
		for( Element recordInfoSet : recordWrap.getChildren("recordSource  ", lidoNamespace)){

			Individual informationObjectInd = model.createIndividual(Namespace_Constants.BFM_NAMESPACE + "recordInfo/" + lidoRecID + "_" + counter, CIDOC_CRM.E73_Information_Object);

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
			recordInd.addProperty(CIDOC_CRM.P67i_is_referred_to_by, informationObjectInd);
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

		// resource set (0-n)
		int mainCounter = 1;
		for(Element resourceSet: resourceSetContainer){

			Individual resourceInd = model.createIndividual(Namespace_Constants.BFM_NAMESPACE + "resource/" + lidoRecID + "_" + mainCounter, CIDOC_CRM.E31_Document );

			//resource id (0-1)
			if(resourceSet.getChild("resourceID", lidoNamespace) != null)
				resourceInd.addProperty(CIDOC_CRM.P48_has_preferred_identifier, resourceSet.getChild("resourceID", lidoNamespace).getValue());

			
			//NICHT BETRACHTET
			//resource representation (0-n) 
			for(Element resourceRepresentation :  resourceSet.getChildren("resourceRepresentation", lidoNamespace) ){

				//link resource (1)
				resourceInd.addProperty( CIDOC_CRM.P3_has_note, resourceRepresentation.getChild( "linkResource", lidoNamespace).getValue());

				//resource measurements (0-n)
				int counter = 1;
				for( Element resourceMeasurementsSet : resourceRepresentation.getChildren( "resourceMeasurementsSet ", lidoNamespace )){

					Individual dimensionInd = model.createIndividual(Namespace_Constants.BFM_NAMESPACE + "resourceDimension/" + lidoRecID + "_" + mainCounter + "_" + counter, CIDOC_CRM.E54_Dimension );

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
				Individual resourceTypeInd = LidoElementHandler.getType(resourceType, model, Namespace_Constants.BFM_NAMESPACE, "resourceType/", lidoNamespace, lidoRecID, "", CIDOC_CRM.E55_Type );
				resourceInd.addProperty(CIDOC_CRM.P2_has_type, resourceTypeInd);
			}

			//resource relation type (0-n)
			int counter = 1;
			for(Element resourceRelType : resourceSet.getChildren("resourceRelType", lidoNamespace) ){
				Individual resourceRelTypeInd = LidoElementHandler.getType(resourceRelType, model, Namespace_Constants.BFM_NAMESPACE, "resourceRelType/", lidoNamespace, lidoRecID, 
						String.valueOf(mainCounter) + "_" + String.valueOf(counter), CIDOC_CRM.E55_Type );
				resourceInd.addProperty(CIDOC_CRM.P2_has_type, resourceRelTypeInd);	
				counter++;
			}

			//resource perspective (0-n)
			counter = 1;
			for(Element resourcePerspective  : resourceSet.getChildren("resourcePerspective", lidoNamespace) ){
				Individual resourcePerspectiveInd = LidoElementHandler.getType(resourcePerspective, model, Namespace_Constants.BFM_NAMESPACE, "resourcePerspective/", lidoNamespace, lidoRecID, 
						String.valueOf(mainCounter) + "_" + String.valueOf(counter), CIDOC_CRM.E55_Type  );
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
					Individual timeSpan  = LidoElementHandler.getTimeSpan(  resourceDateTaken.getChild( "date", lidoNamespace ),  model, Namespace_Constants.BFM_NAMESPACE, 
							"resourceDateTaken/" ,  lidoNamespace,  lidoRecID, String.valueOf(mainCounter));
					resourceInd.addProperty( CIDOC_CRM.P4_has_time_span, timeSpan );
				}

			}

			//resource source (0-n)
			counter = 1;
			for(Element resourceSource : resourceSet.getChildren("resourceSource", lidoNamespace)){

				Individual legalBodyInd = model.createIndividual(Namespace_Constants.BFM_NAMESPACE + "resourceSource/" + lidoRecID + "_" + mainCounter + "_" + counter, CIDOC_CRM.E39_Actor);

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

				legalBodyInd.addProperty(CIDOC_CRM.P75_possesses, resourceInd);

				counter++;

			}

			//rights resource (0-n)
			int rightCounter = 1;

			for(Element rightsResource : resourceSet.getChildren("rightsResource ", lidoNamespace) ){

				//right object
				Individual rightInd = model.createIndividual(Namespace_Constants.BFM_NAMESPACE + "rightsResource/" + lidoRecID + "_" + mainCounter + "_" + rightCounter, CIDOC_CRM.E30_Right);

				//rights type (0-n)
				counter = 1;
				for ( Element rightsType : rightsResource.getChildren("rightsType", lidoNamespace)){


					Individual rightsTypeInd = LidoElementHandler.getType(rightsType, model, Namespace_Constants.BFM_NAMESPACE, "resourceRightsType/", lidoNamespace, lidoRecID,
							String.valueOf(mainCounter) + "_" + String.valueOf(rightCounter) + "_" + String.valueOf(counter), CIDOC_CRM.E55_Type );

					rightInd.addProperty(CIDOC_CRM.P2_has_type, rightsTypeInd);

					counter++;

				}

				//rights date (0-1)
				if(rightsResource.getChild("rightsDate ", lidoNamespace) != null){

					Element rightsDate = rightsResource.getChild("rightsDate ", lidoNamespace);

					Individual timeSpan  = LidoElementHandler.getTimeSpan(  rightsDate,  model, Namespace_Constants.BFM_NAMESPACE, 
							"resourceRightsDate/" ,  lidoNamespace,  lidoRecID, String.valueOf(mainCounter) + "_" + String.valueOf(rightCounter) + "_" + String.valueOf(counter) );
					rightInd.addProperty( CIDOC_CRM.P4_has_time_span, timeSpan );

				}

				//rights holder (0-n)
				counter = 1;

				for ( Element rightsHolder : rightsResource.getChildren("rightsHolder", lidoNamespace)){

					Individual legalBodyInd = model.createIndividual(Namespace_Constants.BFM_NAMESPACE + "resourceRightsHolder/" + lidoRecID + "_" + mainCounter + "_" + rightCounter + "_" + counter, CIDOC_CRM.E39_Actor);

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
			mainObject.addProperty(CIDOC_CRM.P70i_is_documented_in, resourceInd);
			mainCounter++;
		}

	}

}