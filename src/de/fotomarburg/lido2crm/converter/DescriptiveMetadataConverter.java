package de.fotomarburg.lido2crm.converter;

import java.util.List;

import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDFS;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.filter.Filters;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;

import de.fotomarburg.lido2crm.ontology.CIDOC_CRM;
import de.fotomarburg.lido2crm.ontology.CIDOC_CRM_PC;
import de.fotomarburg.lido2crm.ontology.Namespace_Constants;
import de.fotomarburg.lido2crm.ontology.EventTypeMapping;
import de.fotomarburg.lido2crm.ontology.LIDO_Terminology_Constants;


/**
 * 
 * @author balandi
 *
 */
public class DescriptiveMetadataConverter {

	public Document descptiveMetaData;
	public Namespace lidoNamespace;
	public XPathFactory xpathFactory;

	public DescriptiveMetadataConverter( Element descptiveMetaData, Namespace lidoNamespace){

		this.descptiveMetaData = new Document();
		this.descptiveMetaData.addContent(descptiveMetaData.clone());
		this.lidoNamespace = lidoNamespace;
		this.xpathFactory = XPathFactory.instance();

	}

	public void addObjectClassificationWrap( Individual mainObject, OntModel model, String lidoRecID ){

		//objectClassificationWrap (1)

		//object work type (1-n)
		String objectWorkTypeWrapPath = "/lido:descriptiveMetadata/lido:objectClassificationWrap/lido:objectWorkTypeWrap/lido:objectWorkType";
		XPathExpression<Element> expr = xpathFactory.compile( objectWorkTypeWrapPath, Filters.element(), null, lidoNamespace );
		List<Element> objectWorkTypeContainer = expr.evaluate(descptiveMetaData);

		int counter = 1;
		for(Element objectWorkType:objectWorkTypeContainer){
			Individual objectWorkTypeInd = LidoElementHandler.getType(objectWorkType, model, Namespace_Constants.BFM_NAMESPACE, "objectWorkType/", lidoNamespace, lidoRecID, String.valueOf(counter), CIDOC_CRM.LE55_ObjectWorkType);
			mainObject.addProperty(CIDOC_CRM.P2_has_type, objectWorkTypeInd);
			counter++;
		}

		//classification (0-n)
		String classificationPath = "/lido:descriptiveMetadata/lido:objectClassificationWrap/lido:classificationWrap/lido:classification";
		expr = xpathFactory.compile( classificationPath, Filters.element(), null, lidoNamespace );
		List<Element> classificationContainer = expr.evaluate(descptiveMetaData);

		counter = 1;
		for(Element classification:classificationContainer){
			Individual classificationInd = LidoElementHandler.getType(classification, model, Namespace_Constants.BFM_NAMESPACE, "classification/", lidoNamespace, lidoRecID, String.valueOf(counter), CIDOC_CRM.LE55_Classification);
			mainObject.addProperty(CIDOC_CRM.P2_has_type, classificationInd);
			counter++;
		}

	}

	public void addObjectIdentificationWrap(Individual mainObject, OntModel model, String lidoRecID){

		//object identification wrap (1)

		//1. title wrap (1)
		String titlePath = "/lido:descriptiveMetadata/lido:objectIdentificationWrap/lido:titleWrap/lido:titleSet";
		XPathExpression<Element> expr = xpathFactory.compile( titlePath, Filters.element(), null, lidoNamespace );
		List<Element> titleContainer = expr.evaluate(descptiveMetaData);

		// title set (1-n)
		int counter = 1;
		for(Element title:titleContainer){

			Individual titleResource = model.createIndividual(Namespace_Constants.BFM_NAMESPACE + "title/" + lidoRecID + "_" + counter, CIDOC_CRM.E35_Title);

			//appellation value (1-n)
			for(Element appellationValue : title.getChildren("appellationValue", lidoNamespace))
				titleResource.addProperty(CIDOC_CRM.P1_is_identified_by, appellationValue.getValue());

			//source appellation (0-n)
			for(Element sourceAppellation : title.getChildren("sourceAppellation", lidoNamespace))
				titleResource.addProperty(CIDOC_CRM.P3_has_note, sourceAppellation.getValue());

			mainObject.addProperty(CIDOC_CRM.P102_has_title, titleResource);
			counter++;
		}

		// inscriptions wrap (1)
		String inscriptionPath = "/lido:descriptiveMetadata/lido:objectIdentificationWrap/lido:inscriptionsWrap/lido:inscriptions";
		expr = xpathFactory.compile( inscriptionPath, Filters.element(), null, lidoNamespace );
		List<Element> inscriptionContainer = expr.evaluate(descptiveMetaData);

		//inscriptions (0-n)
		counter = 1;
		for(Element inscription:inscriptionContainer){

			Individual inscriptionResource = model.createIndividual(Namespace_Constants.BFM_NAMESPACE + "inscription/" + lidoRecID + "_" + counter, CIDOC_CRM.E34_Inscription);

			//inscription transcription (0-n)
			for( Element inscriptionTranscription : inscription.getChildren("inscriptionTranscription", lidoNamespace))
				inscriptionResource.addProperty(RDFS.label, inscriptionTranscription.getValue());

			//Inscription description (0-n)
			for( Element inscriptionDescription : inscription.getChildren("inscriptionDescription", lidoNamespace) ){

				//descriptiveNoteID (0-n)
				for(Element descriptiveNoteID  : inscriptionDescription.getChildren("descriptiveNoteID ", lidoNamespace ))	
					inscriptionResource.addProperty(CIDOC_CRM.P48_has_preferred_identifier, descriptiveNoteID .getValue());

				//descriptiveNoteValue (0-n)
				for(Element descriptiveNoteValue : inscriptionDescription.getChildren("descriptiveNoteValue", lidoNamespace ))	
					inscriptionResource.addProperty(CIDOC_CRM.P3_has_note, descriptiveNoteValue.getValue());

				//sourceDescriptiveNote (0-n)
				for(Element sourceDescriptiveNote:inscriptionDescription.getChildren("sourceDescriptiveNote", lidoNamespace ))	
					inscriptionResource.addProperty(CIDOC_CRM.P3_has_note, sourceDescriptiveNote.getValue());
			}

			mainObject.addProperty(CIDOC_CRM.P128_carries, inscriptionResource);
			counter++;
		}

		//3. RepositoryWrap (0-1)

		//RepositoryName (0-1)
		String legalBodyPath = "/lido:descriptiveMetadata/lido:objectIdentificationWrap/lido:repositoryWrap/lido:repositorySet/lido:repositoryName";
		expr = xpathFactory.compile( legalBodyPath, Filters.element(), null, lidoNamespace );
		List<Element> legalBodyContainer = expr.evaluate(descptiveMetaData);

		counter = 1;
		for(Element repositoryName :legalBodyContainer){

			Individual repositoryNameLegalBody = model.createIndividual(Namespace_Constants.BFM_NAMESPACE + "repositoryName/" + lidoRecID + "_" + counter , CIDOC_CRM.E39_Actor);

			//legal body id (0-n)
			for( Element legalBodyID : repositoryName.getChildren("legalBodyID", lidoNamespace) )
				repositoryNameLegalBody.addProperty( CIDOC_CRM.P48_has_preferred_identifier, legalBodyID .getValue());

			//legal body name (0-n)
			for(Element legalBodyName : repositoryName.getChildren("legalBodyName", lidoNamespace)){

				//appellation value (1-n) 
				for( Element appellationValue : legalBodyName.getChildren("appellationValue", lidoNamespace) )
					repositoryNameLegalBody.addProperty( CIDOC_CRM.P1_is_identified_by, appellationValue.getValue());

				//source appellation (0-n)
				for( Element sourceAppellation : legalBodyName.getChildren("sourceAppellation", lidoNamespace) )
					repositoryNameLegalBody.addProperty( CIDOC_CRM.P3_has_note, sourceAppellation.getValue() );

			}

			//legal body web link (0-n)
			for( Element legalBodyWeblink : repositoryName.getChildren("legalBodyWeblink", lidoNamespace) )
				repositoryNameLegalBody.addProperty( CIDOC_CRM.P3_has_note, legalBodyWeblink.getValue() );

			// Connection to main object 
			mainObject.addProperty( CIDOC_CRM.P49_has_former_or_current_keeper, repositoryNameLegalBody );
			counter++;
		}

		//WorkID (0-n)
		String workIDPath = "/lido:descriptiveMetadata/lido:objectIdentificationWrap/lido:repositoryWrap/lido:repositorySet/lido:workID";
		expr = xpathFactory.compile( workIDPath, Filters.element(), null, lidoNamespace );
		List<Element> workIDContainer = expr.evaluate(descptiveMetaData);

		for(Element workID:workIDContainer){

			Individual workIDResource = model.createIndividual(Namespace_Constants.BFM_NAMESPACE + "workID/" + workID.getValue() , CIDOC_CRM.E42_Identifier);
			workIDResource.addProperty(RDFS.label, workID.getValue());
			mainObject.addProperty(CIDOC_CRM.P1_is_identified_by, workIDResource);

		}

		//Repository Location, direct connected (0-1)
		String placePath = "/lido:descriptiveMetadata/lido:objectIdentificationWrap/lido:repositoryWrap/lido:repositorySet/lido:repositoryLocation";
		expr = xpathFactory.compile( placePath, Filters.element(), null, lidoNamespace );
		Element place = expr.evaluateFirst(descptiveMetaData);

		if(place != null){
			mainObject.addProperty(CIDOC_CRM.P55_has_current_location, getPlace(place, model, lidoNamespace, lidoRecID));
		}

		//4. DisplayStateEditionWrap (0-1)
		String displayStatePath = "/lido:descriptiveMetadata/lido:objectIdentificationWrap/lido:displayStateEditionWrap";
		expr = xpathFactory.compile( displayStatePath, Filters.element(), null, lidoNamespace );
		List<Element> displayStateContainer = expr.evaluate(descptiveMetaData);

		counter = 1;
		for(Element displayStateEdition: displayStateContainer){

			Individual displayStateResource = model.createIndividual(Namespace_Constants.BFM_NAMESPACE + "contitionState/" + lidoRecID + "_" + counter , CIDOC_CRM.E3_Condition_State);

			//displayState (0-n)
			for( Element displayState : displayStateEdition.getChildren("displayState", lidoNamespace) )
				displayStateResource.addProperty(RDFS.label, displayState.getValue());

			//displayEdition (0-n)
			for(Element displayEdition: displayStateEdition.getChildren("displayEdition", lidoNamespace) ){
				if(displayEdition.getChild("displayEdition", lidoNamespace) !=null)
				displayStateResource.addProperty(CIDOC_CRM.P3_has_note, displayEdition.getChild("displayEdition", lidoNamespace).getValue());
			}
			//sourceStateEdition (0-n)
			for(Element sourceStateEdition : displayStateEdition.getChildren("sourceStateEdition", lidoNamespace))
				displayStateResource.addProperty(CIDOC_CRM.P3_has_note, sourceStateEdition.getChild("sourceStateEdition", lidoNamespace).getValue());

			mainObject.addProperty(CIDOC_CRM.P44_has_condition, displayStateResource);
			counter++;

		}

		//5. ObjectDescriptionWrap (0-1)
		String objectDescriptionPath = "/lido:descriptiveMetadata/lido:objectIdentificationWrap/lido:objectDescriptionWrap/lido:objectDescriptionSet";
		expr = xpathFactory.compile( objectDescriptionPath, Filters.element(), null, lidoNamespace );
		List<Element> objectDescriptionContainer = expr.evaluate(descptiveMetaData);

		//object description set (0-n)
		counter = 1;
		for(Element objectDescription : objectDescriptionContainer){

			Individual objectDescriptionResource = model.createIndividual(Namespace_Constants.BFM_NAMESPACE + "objectDescription/" + lidoRecID + "_" + counter , CIDOC_CRM.E73_Information_Object);

			//descriptiveNoteValue (0-n)
			for(Element descriptiveNoteID  : objectDescription.getChildren("descriptiveNoteID ", lidoNamespace) )
				objectDescriptionResource.addProperty(CIDOC_CRM.P48_has_preferred_identifier, descriptiveNoteID .getValue());

			//descriptiveNoteValue (0-n)
			for(Element descriptiveNoteValue : objectDescription.getChildren("descriptiveNoteValue", lidoNamespace) )
				objectDescriptionResource.addProperty(CIDOC_CRM.P1_is_identified_by, descriptiveNoteValue.getValue());

			//sourceDescriptiveNote (0-n)
			for(Element sourceDescriptiveNote : objectDescription.getChildren("sourceDescriptiveNote", lidoNamespace))
				objectDescriptionResource.addProperty(CIDOC_CRM.P3_has_note, sourceDescriptiveNote.getValue());

			mainObject.addProperty(CIDOC_CRM.P67i_is_referred_to_by, objectDescriptionResource);
			counter++;

		}

		//6. ObjectMeasurementsWrap (0-1)
		String measurementPath = "/lido:descriptiveMetadata/lido:objectIdentificationWrap/lido:objectMeasurementsWrap/lido:objectMeasurementsSet";
		expr = xpathFactory.compile( measurementPath, Filters.element(), null, lidoNamespace );
		List<Element> measurementContainer = expr.evaluate(descptiveMetaData);

		//objectMeasurementsSet (0-n)
		counter = 1;
		for(Element measurement : measurementContainer){

			Individual measurementResource =  model.createIndividual(Namespace_Constants.BFM_NAMESPACE + "measurement/" + lidoRecID + "_" + counter , CIDOC_CRM.E54_Dimension);

			//displayObjectMeasurements (0-n)
			for(Element displayObjectMeasurements : measurement.getChildren("displayObjectMeasurements", lidoNamespace))
				measurementResource.addProperty(CIDOC_CRM.P3_has_note, displayObjectMeasurements.getValue());

			//objectMeasurements (0-1)
			if(measurement.getChild("objectMeasurements", lidoNamespace) != null){

				Element objectMeasurements = measurement.getChild("objectMeasurements", lidoNamespace);

				//measurementsSet (0-n)
				for( Element measurementsSet : objectMeasurements.getChildren("measurementsSet", lidoNamespace) ){

					//measurementType (1-n)
					for( Element measurementType: measurementsSet.getChildren("measurementType"))
						measurementResource.addProperty(CIDOC_CRM.P2_has_type, measurementType.getValue());

					//measurementUnit (1-n)
					for(Element measurementUnit : measurementsSet.getChildren("measurementUnit") )
						measurementResource.addProperty(CIDOC_CRM.P91_has_unit, measurementUnit.getValue());

					//measurementValue
					if(measurementsSet.getChild("measurementValue") != null)
						measurementResource.addProperty(CIDOC_CRM.P90_has_value, measurementsSet.getChild("measurementValue").getValue());

				}

				//extentMeasurements (0-n)
				for(Element extentMeasurements :objectMeasurements.getChildren("extentMeasurements", lidoNamespace))
					measurementResource.addProperty(CIDOC_CRM.P3_has_note, extentMeasurements.getValue());

				//qualifierMeasurements (0-n)
				for(Element qualifierMeasurements: objectMeasurements.getChildren("qualifierMeasurements", lidoNamespace))
					measurementResource.addProperty(CIDOC_CRM.P3_has_note, qualifierMeasurements.getValue());

				//formatMeasurements (0-n)
				for(Element formatMeasurements : objectMeasurements.getChildren("formatMeasurements", lidoNamespace))
					measurementResource.addProperty(CIDOC_CRM.P3_has_note, formatMeasurements.getValue());

				//shapeMeasurements (0-n)
				for(Element shapeMeasurements : objectMeasurements.getChildren("shapeMeasurements", lidoNamespace))
					measurementResource.addProperty(CIDOC_CRM.P3_has_note, shapeMeasurements.getValue());

				//scaleMeasurements (0-n)
				for(Element scaleMeasurements : objectMeasurements.getChildren("scaleMeasurements", lidoNamespace) )
					measurementResource.addProperty(CIDOC_CRM.P3_has_note, scaleMeasurements.getValue());

			}

			mainObject.addProperty(CIDOC_CRM.P43_has_dimension, measurementResource);
			counter++;
		}

	}

	public void addEventWrap(Individual mainObject, OntModel model, String lidoRecID){

		//display event (0-n) !?

		//Event wrap (0-1)
		String eventPath = "/lido:descriptiveMetadata/lido:eventWrap/lido:eventSet/lido:event";
		XPathExpression<Element> expr = xpathFactory.compile( eventPath, Filters.element(), null, lidoNamespace );
		List<Element> eventContainer = expr.evaluate(descptiveMetaData);

		//event set (0-n)
		int mainCounter = 1;

		for(Element event:eventContainer){
			Individual eventResource = getEvent( model,  lidoRecID,  event,  mainCounter);
			eventResource.addProperty(CIDOC_CRM.P12_occurred_in_the_presence_of, mainObject);
			mainCounter++;
		}
	}

	public void addObjectRelationWrap(Individual mainObject, OntModel model, String lidoRecID){

		//objectRelationWrap (0-1)

		//1. SubjectWrap (0-1)

		//subjectSet (0-n)
		//display subject (0-n) !?

		//subject (0-1)
		String subjectPath = "/lido:descriptiveMetadata/lido:objectRelationWrap/lido:subjectWrap/lido:subjectSet/lido:subject";
		XPathExpression<Element> expr = xpathFactory.compile( subjectPath, Filters.element(), null, lidoNamespace );
		List<Element> subjectContainer = expr.evaluate(descptiveMetaData);

		int mainCounter = 1;
		for(Element subject:subjectContainer){

			//create information object
			Individual subjectInd = model.createIndividual(Namespace_Constants.BFM_NAMESPACE + "subject/" + lidoRecID + "_" + mainCounter, CIDOC_CRM.E73_Information_Object);

			//extentSubject  (0-n)
			for ( Element extendSubject : subject.getChildren("extentSubject", lidoNamespace)){
				subjectInd.addProperty(CIDOC_CRM.P3_has_note, "extentSubject: " + extendSubject.getValue() );
			}

			//subjectConcept (0-n)
			int counter = 1;
			for ( Element subjectConcept : subject.getChildren("subjectConcept", lidoNamespace)){
				Individual subjectConceptInd = LidoElementHandler.getType(subjectConcept, model, Namespace_Constants.BFM_NAMESPACE, "subjectConcept/", lidoNamespace, 
						lidoRecID, String.valueOf(mainCounter) + "_" + String.valueOf(counter), CIDOC_CRM.E55_Type );
				subjectInd.addProperty(CIDOC_CRM.P2_has_type, subjectConceptInd );
				counter++;
			}

			//subjectActor (0-n)
			for (Element subjectActor : subject.getChildren("subjectActor", lidoNamespace)){
				if(subjectActor.getChild("actor", lidoNamespace) != null){
					Individual actorInd = getActor(subjectActor.getChild("actor", lidoNamespace), model, lidoNamespace, lidoRecID + "_subject", String.valueOf( mainCounter ) );
					subjectInd.addProperty(CIDOC_CRM.P67_refers_to, actorInd );
				}
			}

			//subjectDate (0-n)
			counter=1;
			for(Element subjectDate : subject.getChildren("subjectDate ",lidoNamespace)){

				Individual timeSpan  = LidoElementHandler.getTimeSpan( subjectDate,  model, Namespace_Constants.BFM_NAMESPACE, 
						"subjectDate/" ,  lidoNamespace,  lidoRecID,  String.valueOf(mainCounter) + "_" + String.valueOf(counter));

				//
				subjectInd.addProperty( CIDOC_CRM.P67_refers_to, timeSpan );
				counter++;

			}

			//subjectEvent (0-n)
			counter=1;
			for(Element subjectEvent : subject.getChildren("subjectEvent",lidoNamespace)){

				if(subjectEvent.getChild("event", lidoNamespace) != null){
					Individual subjectEventInd = getEvent(model, lidoRecID + "_subject", subjectEvent.getChild("event", lidoNamespace), mainCounter);
					subjectInd.addProperty( CIDOC_CRM.P67_refers_to, subjectEventInd );
				}

				counter++;
			}

			//subjectPlace (0-n)
			for ( Element subjectPlace : subject.getChildren("subjectPlace", lidoNamespace)){

				if(subjectPlace.getChild("place", lidoNamespace).getChild("placeID", lidoNamespace) != null ){
					subjectInd.addProperty(CIDOC_CRM.P67_refers_to, getPlace(subjectPlace.getChild("place", lidoNamespace), model, lidoNamespace, lidoRecID + "_subject") );
				}
			}

			//subjectObject  (0-n)
			for ( Element subjectObject : subject.getChildren("subjectObject", lidoNamespace)){

				if(subjectObject.getChild("object", lidoNamespace).getChild("objectID", lidoNamespace) != null )
					subjectInd.addProperty(CIDOC_CRM.P67_refers_to,Namespace_Constants.BFM_NAMESPACE + subjectObject.getChild("object", lidoNamespace).getChild("objectID", lidoNamespace).getValue() );
			}

			//connect to main object
			mainObject.addProperty(CIDOC_CRM.P67i_is_referred_to_by, subjectInd);
			mainCounter++;
		}


		//2. RelatedWorksWrap (0-1)

		//relatedWorkSet(0-n)
		//	relatedWork (0-1)
		//	relatedWorkRelType  (0-1)
		String relatedWorkConceptIDPath = "/lido:descriptiveMetadata/lido:objectRelationWrap/lido:relatedWorksWrap/relatedWorkSet/lido:relatedWorkRelType/lido:conceptID[@lido:type = 'http://terminology.lido-schema.org/lido00100']";
		expr = xpathFactory.compile( relatedWorkConceptIDPath, Filters.element(), null, lidoNamespace );
		List<Element> relatedWorkConceptIDContainer = expr.evaluate(descptiveMetaData);

		mainCounter = 1;

		for(Element relatedWorkConceptID:relatedWorkConceptIDContainer){

			Element relatedWorkSet = relatedWorkConceptID.getParentElement().getParentElement();

			Element relatedObject = relatedWorkSet.getChild("relatedWork", lidoNamespace).getChild("object", lidoNamespace);

			if(relatedObject.getChild("objectID", lidoNamespace) != null){
				System.out.println("out" +CIDOC_CRM.m_model.getProperty( CIDOC_CRM.NS + relatedWorkConceptID.getValue()));
				mainObject.addProperty( CIDOC_CRM.m_model.getProperty( CIDOC_CRM.NS + relatedWorkConceptID.getValue()), Namespace_Constants.BFM_NAMESPACE + relatedObject.getChild("objectID", lidoNamespace).getValue().replace("DE-Mb112/", "") );

			}
		}

	}

	private Individual getEvent(OntModel model, String lidoRecID, Element event, int eventCounter){

		Document eventDoc = new Document();
		eventDoc.addContent(event.clone());

		//Event
		Element eventType = event.getChild("eventType",lidoNamespace);
		String value = eventType.getChild("conceptID", lidoNamespace).getValue();
		Individual eventResource = model.createIndividual(Namespace_Constants.BFM_NAMESPACE + "event/" + lidoRecID + "_" + eventCounter, EventTypeMapping.getType(value));

		//1. EventID (0-n)			
		for(Element eventID : event.getChildren("eventID",lidoNamespace))
			eventResource.addProperty(CIDOC_CRM.P48_has_preferred_identifier, eventID.getValue());

		//2. EventType (1) 
		//Element eventType = event.getChild("eventType",lidoNamespace);
		//Individual eventTypeInd = LidoElementHandler.getType(eventType, model, Namespace_Constants.BFM_NAMESPACE, "eventType/", lidoNamespace, lidoRecID, String.valueOf(mainCounter), CIDOC_CRM.E55_Type);
		//eventResource.addProperty(CIDOC_CRM.P2_has_type, eventTypeInd);

		//3. RoleInEvent (0-n)
		int counter =1;
		for(Element roleInEvent : event.getChildren("roleInEvent",lidoNamespace)){
			Individual roleInEventInd = LidoElementHandler.getType(roleInEvent, model, Namespace_Constants.BFM_NAMESPACE, "roleInEvent/", lidoNamespace, lidoRecID,
					String.valueOf(eventCounter) + "_" + String.valueOf(counter), CIDOC_CRM.E55_Type  );
			eventResource.addProperty(CIDOC_CRM.P2_has_type, roleInEventInd);
			counter++;
		}

		//4. EventName (0-n)
		String eventNamePath = "/lido:event/lido:eventName";
		XPathExpression<Element> expr = xpathFactory.compile( eventNamePath, Filters.element(), null, lidoNamespace );
		List<Element> eventNameContainer = expr.evaluate(eventDoc);

		counter = 1;
		for(Element eventName : eventNameContainer){	

			Individual eventNameResource = model.createIndividual(Namespace_Constants.BFM_NAMESPACE + "eventName/" + lidoRecID + "_" + eventCounter + "_" + counter, CIDOC_CRM.E41_Appellation);

			//appellationValue (1-n)
			for( Element appellationValue : eventName.getChildren("appellationValue", lidoNamespace) )
				eventNameResource.addProperty(CIDOC_CRM.P1_is_identified_by, appellationValue.getValue());

			//sourceAppellation (0-n)
			for(Element sourceAppellation : eventName.getChildren("sourceAppellation", lidoNamespace) )
				eventNameResource.addProperty(CIDOC_CRM.P3_has_note, sourceAppellation.getValue());

			eventResource.addProperty(CIDOC_CRM.P1_is_identified_by, eventNameResource);
			counter++;
		}

		//5. EventActor (0-n) - BFM specific 
		String actorInRolePath = "/lido:event/lido:eventActor/lido:actorInRole";
		expr = xpathFactory.compile( actorInRolePath, Filters.element(), null, lidoNamespace );
		List<Element> actorInRoleContainer = expr.evaluate(eventDoc);

		//displayActorInRole (0-n) !? 

		//actorInRole(0-1)
		int actorCounter = 1;
		for(Element actorInRole : actorInRoleContainer){

			//from event to actor
			Document actorDoc = new Document();
			actorDoc.addContent(actorInRole.clone());

			//actor (1) - BFM specific
			String actorIDPath = "/lido:actorInRole/lido:actor";
			expr = xpathFactory.compile( actorIDPath, Filters.element(), null, lidoNamespace );
			Element actorID = expr.evaluateFirst(actorDoc);
			Resource actorIDResource = getActor(actorID ,  model,  lidoNamespace,  lidoRecID,  String.valueOf(eventCounter) + "_" + String.valueOf(actorCounter)  ); //XYZ

			//roleActor (0-n) if activity
			if(isActivity(value)){

				//Statement
				//Statement event_actor_statement = model.createStatement(eventResource, CIDOC_CRM.P14_carried_out_by, actorIDResource);
				//System.out.println(Namespace_Constants.BFM_NAMESPACE  + "nane"+ lidoRecID + actorCounter + mainCounter);
				//ReifiedStatement rstmt = model.createReifiedStatement(Namespace_Constants.BFM_NAMESPACE  + "nane"+ lidoRecID + actorCounter + mainCounter, event_actor_statement);

				Individual pc14_carried_out_by = model.createIndividual(Namespace_Constants.BFM_NAMESPACE  + "PC14_carried_out_by/"+ lidoRecID + actorCounter + eventCounter, CIDOC_CRM_PC.PC14_carried_out_by);
				pc14_carried_out_by.addProperty(CIDOC_CRM_PC.P01_has_domain, eventResource);
				pc14_carried_out_by.addProperty(CIDOC_CRM_PC.P02_has_range, actorIDResource);

				counter = 1;
				for(Element roleActor: actorInRole.getChildren("roleActor" , lidoNamespace)){

					Individual roleActorInd =  model.createIndividual(Namespace_Constants.BFM_NAMESPACE + "roleActor/" + lidoRecID + "_" + eventCounter + "_" + actorCounter + "_" + counter, CIDOC_CRM.E55_Type);


					//conceptID (0-n)
					for(Element conceptID  :roleActor.getChildren("conceptID ", lidoNamespace))
						roleActorInd.addProperty(CIDOC_CRM.P48_has_preferred_identifier, conceptID .getValue());

					//term (0-n)
					for(Element term :roleActor.getChildren("term", lidoNamespace))
						roleActorInd.addProperty( RDFS.label, term.getValue());

					//model.createStatement(rstmt, CIDOC_CRM.P14_1_in_the_role_of , roleActorInd);
					pc14_carried_out_by.addProperty(CIDOC_CRM_PC.P14_1_in_the_role_of , roleActorInd);

					counter++;
				}
			} else {

				eventResource.addProperty(CIDOC_CRM.P11_had_participant,actorIDResource);

			}

			//attributionQualifierActor (0-n)
			for(Element attributionQualifierActor : actorInRole.getChildren("attributionQualifierActor", lidoNamespace) )
				eventResource.addProperty(CIDOC_CRM.P3_has_note, "attribution qualifier actor: " + attributionQualifierActor.getValue());

			//extentActor (0-n)
			for( Element extentActor : actorInRole.getChildren("extentActor" , lidoNamespace) )
				eventResource.addProperty(CIDOC_CRM.P3_has_note, "extend Actor: " + extentActor.getValue());

			actorCounter++;
		}

		//6. Culture (0-n)
		counter=1;
		for(Element culture: event.getChildren("culture",lidoNamespace)){

			Individual culturInd = model.createIndividual(Namespace_Constants.BFM_NAMESPACE + "culture/" + lidoRecID + "_" + eventCounter + "_" + counter, CIDOC_CRM.E4_Period); //later specific type 

			//conceptID (0-n)
			for(Element conceptID  : culture.getChildren("conceptID ", lidoNamespace))
				culturInd.addProperty(CIDOC_CRM.P48_has_preferred_identifier, conceptID .getValue());

			//term (0-n)
			for(Element term :culture.getChildren("term", lidoNamespace))
				culturInd.addProperty(RDFS.label, term.getValue());

			//eventResource
			eventResource.addProperty( CIDOC_CRM.P9_consists_of, culturInd );
			counter++;

		}


		//7. EventDate (0-1)
		if( event.getChild("eventDate",lidoNamespace) != null ){

			Element eventDate = event.getChild("eventDate",lidoNamespace);

			//displayDate (0-n) !?

			//date (0-1)
			if( eventDate.getChild("date",lidoNamespace) != null){

				Individual timeSpan  = LidoElementHandler.getTimeSpan( eventDate.getChild("date",lidoNamespace),  model, Namespace_Constants.BFM_NAMESPACE, 
						"eventDate/" ,  lidoNamespace,  lidoRecID, String.valueOf(eventCounter));

				eventResource.addProperty( CIDOC_CRM.P4_has_time_span, timeSpan );
			}
		}

		//8. PeriodName (0-n)
		counter = 1;
		for(Element periodName: event.getChildren("periodName",lidoNamespace)){

			Individual periodNameInd = model.createIndividual(Namespace_Constants.BFM_NAMESPACE + "periodName/" + lidoRecID + "_" + eventCounter + "_" + counter, CIDOC_CRM.E4_Period); //later specific type 

			//conceptID (0-n)
			for(Element conceptID  : periodName.getChildren("conceptID ", lidoNamespace))
				periodNameInd.addProperty(CIDOC_CRM.P48_has_preferred_identifier, conceptID .getValue());

			//term (0-n)
			for(Element term :periodName.getChildren("term", lidoNamespace))
				periodNameInd.addProperty(RDFS.label, term.getValue());

			//eventResource
			eventResource.addProperty( CIDOC_CRM.P9_consists_of, periodNameInd );
			counter++;
		}

		//9. EventPlace (0-n)
		for(Element eventPlace: event.getChildren("eventPlace",lidoNamespace)){
			if( eventPlace.getChild("place", lidoNamespace) != null ){
				eventResource.addProperty( CIDOC_CRM.P7_took_place_at, getPlace(eventPlace.getChild("place", lidoNamespace), model, lidoNamespace, lidoRecID + "_event"));
			}
		}

		if(isActivity(value)){

			//10. EventMethod (0-n)
			counter = 1;
			for(Element eventMethod: event.getChildren("eventMethod",lidoNamespace)){
				Individual eventMethodInd = LidoElementHandler.getType(eventMethod, model, Namespace_Constants.BFM_NAMESPACE, "eventMethod/", lidoNamespace, lidoRecID,
						String.valueOf(eventCounter) + "_" + String.valueOf(counter), CIDOC_CRM.E55_Type );
				eventResource.addProperty( CIDOC_CRM.P33_used_specific_technique, eventMethodInd );
				counter++;
			}

			//11. EventMaterialTech (0-n)
			counter = 1;
			for(Element eventMaterialsTech: event.getChildren("eventMaterialsTech",lidoNamespace)){

				Individual eventMaterialsTechInd = null;
				String termMaterialsTechType = null;
				//displayMaterialsTech (0-n) !?

				//materialsTech (0-1) 
				if(eventMaterialsTech.getChild("materialsTech", lidoNamespace ) != null ){

					Element materialsTech = eventMaterialsTech.getChild("materialsTech", lidoNamespace );

					//termMaterialsTech (0-n)
					for(Element termMaterialsTech : materialsTech.getChildren("termMaterialsTech", lidoNamespace)){

						termMaterialsTechType =termMaterialsTech.getAttributeValue("type", lidoNamespace);

						//E57 Material
						if(termMaterialsTechType.equals("http://terminology.lido-schema.org/lido00131")){
							eventMaterialsTechInd = LidoElementHandler.getType(termMaterialsTech, model, Namespace_Constants.BFM_NAMESPACE, "eventMaterialsTech/", lidoNamespace, lidoRecID, String.valueOf(eventCounter) + "_" + String.valueOf(counter), CIDOC_CRM.E57_Material);	
							eventResource.addProperty( CIDOC_CRM.P126_employed, eventMaterialsTechInd );

							//E55 Type
						}else{
							eventMaterialsTechInd = LidoElementHandler.getType(termMaterialsTech, model, Namespace_Constants.BFM_NAMESPACE, "eventMaterialsTech/", lidoNamespace, lidoRecID, String.valueOf(eventCounter) + "_" + String.valueOf(counter), CIDOC_CRM.E55_Type);

							if(termMaterialsTechType.equals("http://terminology.lido-schema.org/lido00132")) 
								eventResource.addProperty( CIDOC_CRM.P32_used_general_technique, eventMaterialsTechInd );

							if(termMaterialsTechType.equals("http://terminology.lido-schema.org/lido00248")) 
								eventResource.addProperty( CIDOC_CRM.P125_used_object_of_type, eventMaterialsTechInd );
						}
					}

					//extentMaterialsTech (0-n)
					for(Element extentMaterialsTech: materialsTech.getChildren("extentMaterialsTech", lidoNamespace) )
						eventMaterialsTechInd.addProperty(CIDOC_CRM.P3_has_note, "extentMaterialsTech: " + extentMaterialsTech.getValue());

					//sourceMaterialsTech (0-n)
					for(Element sourceMaterialsTech: materialsTech.getChildren("sourceMaterialsTech", lidoNamespace) )
						eventMaterialsTechInd.addProperty(CIDOC_CRM.P3_has_note, "sourceMaterialsTech: " + sourceMaterialsTech.getValue());
				}

				counter++;
			}

		}


		//12. thingPresent (0-n)
		for(Element thingPresent: event.getChildren("thingPresent",lidoNamespace)){
			if(thingPresent.getChild("object",lidoNamespace) != null)
				if(thingPresent.getChild("object",lidoNamespace).getChild("objectID",lidoNamespace) != null) 
					eventResource.addProperty(CIDOC_CRM.P12_occurred_in_the_presence_of, thingPresent.getChild("object",lidoNamespace).getChild("objectID",lidoNamespace).getValue());
		}


		//13. relatedEventSet (0-n)
		counter = 1;
		for(Element relatedEventSet : event.getChildren("relatedEventSet ",lidoNamespace)){

			//relatedEvent (0-1)
			if(relatedEventSet.getChild("relatedEvent",lidoNamespace) != null){

				Element relatedEvent = relatedEventSet.getChild("relatedEvent",lidoNamespace);

				//displayEvent (0-n) !?

				//event (0-1)
				if(relatedEvent.getChild("event", lidoNamespace) != null){
					Individual relatedEventInd = getEvent(model,lidoRecID + "_P", relatedEvent.getChild("event", lidoNamespace), eventCounter);
					eventResource.addProperty(CIDOC_CRM.P9_consists_of, relatedEventInd);
				}
			}

			//relatedEventRelType (0-1)
			if(relatedEventSet.getChild("relatedEventRelType",lidoNamespace) != null){

				Individual relatedEventRelTypeInd = model.createIndividual(Namespace_Constants.BFM_NAMESPACE + "relatedEventRelType/" + lidoRecID + "_" + eventCounter + "_" + counter, CIDOC_CRM.E55_Type); //later specific type 

				//conceptID (0-n)
				for(Element conceptID  : relatedEventSet.getChild("relatedEventRelType",lidoNamespace).getChildren("conceptID ", lidoNamespace))
					relatedEventRelTypeInd.addProperty(CIDOC_CRM.P48_has_preferred_identifier, conceptID .getValue());

				//term (0-n)
				for(Element term : relatedEventSet.getChild("relatedEventRelType",lidoNamespace).getChildren("term", lidoNamespace))
					relatedEventRelTypeInd.addProperty(CIDOC_CRM.P3_has_note, term.getValue());

				eventResource.addProperty(CIDOC_CRM.P2_has_type, relatedEventRelTypeInd);

			}

			counter++;
		}

		//14. EventDescriptionSet (0-n)
		counter=1;
		for(Element eventDescriptionSet: event.getChildren("eventDescriptionSet",lidoNamespace)){

			Individual eventDescription = model.createIndividual(Namespace_Constants.BFM_NAMESPACE + "eventDescriptionSet/" + lidoRecID + "_" + eventCounter + "_" + counter, CIDOC_CRM.E73_Information_Object); 

			//descriptiveNoteID (0-n)
			for(Element descriptiveNoteID  : eventDescriptionSet.getChildren("descriptiveNoteID ", lidoNamespace))
				eventDescription.addProperty(CIDOC_CRM.P48_has_preferred_identifier, descriptiveNoteID.getValue());

			//descriptiveNoteValue (0-n)
			for(Element descriptiveNoteValue  : eventDescriptionSet.getChildren("descriptiveNoteValue ", lidoNamespace))
				eventDescription.addProperty(CIDOC_CRM.P1_is_identified_by, descriptiveNoteValue.getValue());

			//sourceDescriptiveNote (0-n)
			for(Element sourceDescriptiveNote : eventDescriptionSet.getChildren("sourceDescriptiveNote", lidoNamespace))
				eventDescription.addProperty(CIDOC_CRM.P3_has_note, sourceDescriptiveNote.getValue());

			eventResource.addProperty(CIDOC_CRM.P67i_is_referred_to_by, eventDescription);
			counter++;
		}

		return eventResource;
	}

	private Individual getPlace(Element place , OntModel model, Namespace lidoNamespace, String lidoRecID ){

		//place individual
		Individual placeInd = null;

		for(Element palceID : place.getChildren("placeID", lidoNamespace)){		
			if( palceID.getAttributeValue("type", lidoNamespace ).equals("http://terminology.lido-schema.org/lido00100")){
				placeInd = model.createIndividual( Namespace_Constants.BFM_NAMESPACE + "place/" + palceID.getValue().replace(" ", "") , CIDOC_CRM.E53_Place );
				break;
			}
		}

		if(placeInd == null)
			placeInd = model.createIndividual( Namespace_Constants.BFM_NAMESPACE + "place/" + lidoRecID , CIDOC_CRM.E53_Place );

		//placeID (0-n)
		for(Element palceID : place.getChildren("placeID", lidoNamespace)){		
			if( !palceID.getAttributeValue("type", lidoNamespace ).equals("http://terminology.lido-schema.org/lido00100")){
				placeInd.addProperty(OWL.sameAs, palceID.getValue());
			}
		}

		//namePlaceSet(0-n)
		for( Element namePlaceSet : place.getChildren("namePlaceSet", lidoNamespace) ){

			//appellationValue (1-n)
			for(Element appellationValue: namePlaceSet.getChildren("appellationValue", lidoNamespace)){
				placeInd.addProperty(CIDOC_CRM.P1_is_identified_by, appellationValue.getValue());
			}

			//sourceAppellation (0-n)
			for(Element sourceAppellation: namePlaceSet.getChildren("sourceAppellation", lidoNamespace)){
				placeInd.addProperty(CIDOC_CRM.P3_has_note, sourceAppellation.getValue());
			}

		}

		//gml(0-n)
		//NN

		//partOfPlace(0-n)
		int counter = 1;
		for(Element partOfPlace: place.getChildren("partOfPlace", lidoNamespace)){
			placeInd.addProperty(CIDOC_CRM.P46i_forms_part_of, getPlace( partOfPlace, model,  lidoNamespace, lidoRecID + "P" + counter ));
			counter++;
		}

		//placeClassification (0-n)
		counter = 1;
		for(Element placeClassification: place.getChildren("placeClassification", lidoNamespace)){
			Individual placeClassificationInd = LidoElementHandler.getType(placeClassification, model, Namespace_Constants.BFM_NAMESPACE, "placeClassification/", lidoNamespace, lidoRecID, String.valueOf(counter), CIDOC_CRM.E55_Type );
			placeInd.addProperty(CIDOC_CRM.P2_has_type, placeClassificationInd);
			counter++;
		}

		return placeInd;
	}

	private Individual getActor( Element actor , OntModel model, Namespace lidoNamespace, String lidoRecID, String counter ){

		Individual actorInd = null;

		for(Element actorID : actor.getChildren("actorID", lidoNamespace)){		
			if( actorID.getAttributeValue("type", lidoNamespace ).equals("http://terminology.lido-schema.org/lido00100")){
				actorInd = model.createIndividual( Namespace_Constants.BFM_NAMESPACE + "actor/" + actorID.getValue().replace(" ", "") , CIDOC_CRM.E39_Actor );
				break;
			}
		}

		if(actorInd == null)
			actorInd = model.createIndividual( Namespace_Constants.BFM_NAMESPACE + "actor/" + lidoRecID + "_" + counter, CIDOC_CRM.E39_Actor );

		//actorId (0-n)
		for(Element actorID : actor.getChildren("actorID", lidoNamespace)){		
			if( !actorID.getAttributeValue("type", lidoNamespace ).equals("http://terminology.lido-schema.org/lido00100")){
				actorInd.addProperty(OWL.sameAs, actorID.getValue());
			}
		}

		//nameActorSet (1-n)
		for(Element nameActorSet : actor.getChildren("nameActorSet", lidoNamespace)){

			//appellation value (1-n)
			for(Element appellationValue : nameActorSet.getChildren("appellationValue", lidoNamespace))
				actorInd.addProperty(CIDOC_CRM.P1_is_identified_by, appellationValue.getValue());

			//source appellation (0-n)
			for(Element sourceAppellation : nameActorSet.getChildren("sourceAppellation", lidoNamespace))
				actorInd.addProperty(CIDOC_CRM.P3_has_note, sourceAppellation.getValue());

		}

		//nationalityActor (0-n)
		for(Element nationalityActor : actor.getChildren("nationalityActor", lidoNamespace)){

			//term (0-n)
			for(Element term : nationalityActor.getChildren("term", lidoNamespace )){
				actorInd.addProperty(CIDOC_CRM.P2_has_type, term.getValue());
			}

		}

		//vitalDatesActor (0-1)
		if( actor.getChild("vitalDatesActor", lidoNamespace) != null){

			Element vitalDatesActor = actor.getChild("vitalDatesActor", lidoNamespace) ;

			if( vitalDatesActor.getChild("earliestDate", lidoNamespace) != null)
				actorInd.addProperty(CIDOC_CRM.P92i_was_brought_into_existence_by, vitalDatesActor.getChild("earliestDate", lidoNamespace).getValue());

			if(vitalDatesActor.getChild("latestDate", lidoNamespace) != null)
				actorInd.addProperty(CIDOC_CRM.P93i_was_taken_out_of_existence_by, vitalDatesActor.getChild("latestDate", lidoNamespace).getValue());
		}

		//genderActor (0-n)
		for(Element genderActor : actor.getChildren("genderActor", lidoNamespace )){
			actorInd.addProperty(CIDOC_CRM.P2_has_type, genderActor.getValue());
		}

		return actorInd;
	}

	private boolean isActivity(String eventType){

		if( 
				eventType.equals(LIDO_Terminology_Constants.Event) ||
				eventType.equals(LIDO_Terminology_Constants.Beginning_of_Existence) ||
				eventType.equals(LIDO_Terminology_Constants.End_of_Existence) ||
				eventType.equals(LIDO_Terminology_Constants.Transformation) ||
				eventType.equals(LIDO_Terminology_Constants.Destruction) 
				) 
			return false;


		return true;
	}

}