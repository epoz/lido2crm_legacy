package de.fotomarburg.lido2crm.converter_minimal;

import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.SKOS;
import org.jdom2.Element;
import org.jdom2.Namespace;

import de.fotomarburg.lido2crm.ontology.CIDOC_CRM;
import de.fotomarburg.lido2crm.ontology.Namespace_Constants;


/**
 * 
 * @author balandi
 *
 */
public class LidoElementHandlerMinimal {

	static public Object getTypeClassification(Element type, OntModel model, String localNamespace, String path, Namespace lidoNamespace, String lidoRecID, String counter, OntClass e55Type){

		//object
		Individual genericType = model.createIndividual(localNamespace + lidoRecID + path + counter, e55Type );

		//conceptID
		for(  Element conceptID : type.getChildren("conceptID", lidoNamespace) ){

			if(conceptID.getAttributeValue("type") != null)
				if(conceptID.getAttributeValue("type").equals("http://terminology.lido-schema.org/lido00100"))
					return  model.getResource(Namespace_Constants.BFM_NAMESPACE + conceptID.getValue());

			genericType.addProperty(CIDOC_CRM.P1_is_identified_by, conceptID.getValue());
		}

		//Term
		for(Element term : type.getChildren("term", lidoNamespace )){
			genericType.addProperty(CIDOC_CRM.P3_has_note, term.getValue());
		}

		return genericType;
	}

	static public Individual getType(Element type, OntModel model, String localNamespace, String path, Namespace lidoNamespace, String lidoRecID, String counter, OntClass e55Type){


		//object
		Individual genericType = null;

		//get ID
		for(  Element conceptID : type.getChildren("conceptID", lidoNamespace) ){

			if(conceptID.getAttributeValue("pref", lidoNamespace) != null){

				if(conceptID.getAttributeValue("pref", lidoNamespace).equals("preferred") ){
					//if lido 100
					if(conceptID.getAttributeValue("type", lidoNamespace).equals("http://terminology.lido-schema.org/lido00100")){
						genericType = model.createIndividual(localNamespace + "type/" + conceptID.getValue().replace(" ", ""), e55Type );
						break;
					}

					//if lido 99
					if(conceptID.getAttributeValue("type", lidoNamespace).equals("http://terminology.lido-schema.org/lido00099")){
						genericType = model.createIndividual(conceptID.getValue(), e55Type );
						break;
					}
				}

			}
		}

		if(genericType == null){
			genericType = model.createIndividual(localNamespace + path + lidoRecID + counter, e55Type );
		}

		//SameAs conceptID
		for(  Element conceptID : type.getChildren("conceptID", lidoNamespace) ){
			if(conceptID.getAttributeValue("pref", lidoNamespace) == null){
				genericType.addProperty(SKOS.closeMatch, conceptID.getValue());
			}
		}

		//Term
		for(Element term : type.getChildren("term", lidoNamespace )){
			genericType.addProperty(RDFS.label, term.getValue());
		}

		return genericType;

	}

	static public Individual getTimeSpan(Element timeSpan, OntModel model, String localNamespace, String path, Namespace lidoNamespace, String lidoRecID, String counter){

		Individual timeSpanInd  = model.createIndividual(Namespace_Constants.BFM_NAMESPACE + path + lidoRecID + "_" + counter, CIDOC_CRM.E52_Time_Span); 

		if(timeSpan.getChild("earliestDate", lidoNamespace) != null)
			timeSpanInd.addProperty(CIDOC_CRM.P79_beginning_is_qualified_by, timeSpan.getChild("earliestDate", lidoNamespace).getValue());

		if(timeSpan.getChild("latestDate", lidoNamespace) != null)
			timeSpanInd.addProperty(CIDOC_CRM.P80_end_is_qualified_by, timeSpan.getChild("latestDate", lidoNamespace).getValue());

		return timeSpanInd;
	}

	static public String getCRMEventTypeOfLidoEventType(String lidoType){

		//remove namespace
		String lidoTypeWithoutNS = lidoType.replace("http://terminology.lido-schema.org/", "");

		//SPARQL queries
		String sparqlQueryString1 = ""
				+ "prefix dc: <http://purl.org/dc/elements/1.1/>"
				+ "prefix lt: <http://terminology.lido-schema.org/>"
				+ "prefix skos: <http://www.w3.org/2004/02/skos/core#>"
				+ "prefix purl: <http://purl.org/iso25964/skos-thes#>"
				+ "prefix digi: <http://xtree.digicult-verbund.de/xe/>"
				+ "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
				+ "SELECT ?o "
				+ "WHERE { GRAPH ?graphUri { lt:" + lidoTypeWithoutNS +" skos:relatedMatch ?o } }";

		//execute
		Query query = QueryFactory.create(sparqlQueryString1);
		QueryExecution qexec = QueryExecutionFactory.sparqlService("http://lido-terminologie-test.vocnet.org:3030/lidoterm/sparql", query);
		ResultSet results = qexec.execSelect();


		String resultType = null;

		if(results.hasNext()){
			resultType = results.next().get("o").toString();
		}else{
			resultType = getParentType( lidoTypeWithoutNS );			
		}   

		qexec.close() ;

		return resultType;
	}

	static public String getParentType( String lidoType ){

		String sparqlQueryString1 = ""
				+ "prefix dc: <http://purl.org/dc/elements/1.1/>"
				+ "prefix lt: <http://terminology.lido-schema.org/>"
				+ "prefix skos: <http://www.w3.org/2004/02/skos/core#>"
				+ "prefix purl: <http://purl.org/iso25964/skos-thes#>"
				+ "prefix digi: <http://xtree.digicult-verbund.de/xe/>"
				+ "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
				+ "SELECT ?o "
				+ "WHERE { GRAPH ?graphUri { lt:" + lidoType +" skos:broader ?x."
				+ " ?x skos:relatedMatch ?o. } }";

		Query query = QueryFactory.create(sparqlQueryString1);
		QueryExecution qexec = QueryExecutionFactory.sparqlService("http://lido-terminologie-test.vocnet.org:3030/lidoterm/sparql", query);
		ResultSet results = qexec.execSelect();


		if(results.hasNext()){
			return results.next().get("o").toString();
		}else
			return getParentType(getParent(lidoType));
	}

	static public String getParent( String lidoType ){

		String sparqlQueryString1 = ""
				+ "prefix dc: <http://purl.org/dc/elements/1.1/>"
				+ "prefix lt: <http://terminology.lido-schema.org/>"
				+ "prefix skos: <http://www.w3.org/2004/02/skos/core#>"
				+ "prefix purl: <http://purl.org/iso25964/skos-thes#>"
				+ "prefix digi: <http://xtree.digicult-verbund.de/xe/>"
				+ "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
				+ "SELECT ?o "
				+ "WHERE { GRAPH ?graphUri { lt:" + lidoType +" skos:broader ?o.} }";

		Query query = QueryFactory.create(sparqlQueryString1);
		QueryExecution qexec = QueryExecutionFactory.sparqlService("http://lido-terminologie-test.vocnet.org:3030/lidoterm/sparql", query);

		ResultSet results = qexec.execSelect();


		if(results.hasNext()){
			return results.next().get("o").toString().replace("http://terminology.lido-schema.org/", "");
		}else
			return null;
	}

	//getAppellation ???

}