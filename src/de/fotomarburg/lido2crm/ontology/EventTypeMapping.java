package de.fotomarburg.lido2crm.ontology;

import org.apache.jena.ontology.OntClass;



/**
 * 
 * @author balandi
 *
 */
public class EventTypeMapping {

	static public OntClass getType(String lidoType){
		
		//lido00003 Event  => E5 Event
		if (lidoType.equals(LIDO_Terminology_Constants.Event)) return CIDOC_CRM.E5_Event;
		
		//lido00004 Activity  => E7 Activity
		if (lidoType.equals(LIDO_Terminology_Constants.Activity)) return CIDOC_CRM.E7_Activity;
		
		//lido00001 Acquisition  => E8 Acquisition Event
		if (lidoType.equals(LIDO_Terminology_Constants.Acquisition)) return CIDOC_CRM.E8_Acquisition;
		
		//lido00024 Attribute Assignment => E13 Attribute Assignment
		if (lidoType.equals(LIDO_Terminology_Constants.Attribute_Assignment)) return CIDOC_CRM.E13_Attribute_Assignment;
		
		//lido00023 Type assignment => E17 Type Assignment
		if (lidoType.equals(LIDO_Terminology_Constants.Type_assignment)) return CIDOC_CRM.E17_Type_Assignment;
		
		//lido00226 Commissioning  => ?
		if (lidoType.equals(LIDO_Terminology_Constants.Commissioning)) return CIDOC_CRM.E7_Activity;
		
		//lido00012 Creation  => E65 Creation
		if (lidoType.equals(LIDO_Terminology_Constants.Creation)) return CIDOC_CRM.E65_Creation;
		
		//lido00224 Designing  => ?
		if (lidoType.equals(LIDO_Terminology_Constants.Designing)) return CIDOC_CRM.E7_Activity;
		
		//lido00484 Expression creation  => ?
		if (lidoType.equals(LIDO_Terminology_Constants.Expression_creation)) return CIDOC_CRM.E7_Activity;
		
		//lido00485 Publication Event  => ?
		if (lidoType.equals(LIDO_Terminology_Constants.Publication_Event)) return CIDOC_CRM.E7_Activity;
		
		//lido00032 Planning  => ?
		if (lidoType.equals(LIDO_Terminology_Constants.Planning)) return CIDOC_CRM.E7_Activity;
		
		//lido00013 Type creation => E83 Type Creation
		if (lidoType.equals(LIDO_Terminology_Constants.Type_creation)) return CIDOC_CRM.E83_Type_Creation;
		
		//lido00486 Work Conception => ?
		if (lidoType.equals(LIDO_Terminology_Constants.Work_Conception)) return CIDOC_CRM.E7_Activity;
		
		//lido00225 Exhibition  => ?
		if (lidoType.equals(LIDO_Terminology_Constants.Exhibition)) return CIDOC_CRM.E7_Activity;
		
		//lido00002 Finding  => ?
		if (lidoType.equals(LIDO_Terminology_Constants.Finding)) return CIDOC_CRM.E7_Activity;
		
		//lido00401 Marketing  => ?
		if (lidoType.equals(LIDO_Terminology_Constants.Marketing)) return CIDOC_CRM.E7_Activity;
		
		//lido00006 Modification  => E11 Modification
		if (lidoType.equals(LIDO_Terminology_Constants.Modification)) return CIDOC_CRM.E11_Modification;
		
		//lido00008 Part addition => E79 Part Addition
		if (lidoType.equals(LIDO_Terminology_Constants.Part_addition)) return CIDOC_CRM.E79_Part_Addition;
		
		//lido00021 Part removal  => E80 Part Removal
		if (lidoType.equals(LIDO_Terminology_Constants.Part_removal)) return CIDOC_CRM.E80_Part_Removal;
		
		//lido00007 Production  => E12 Production
		if (lidoType.equals(LIDO_Terminology_Constants.Production)) return CIDOC_CRM.E12_Production;
		
		//lido00025 Completion  => ?
		if (lidoType.equals(LIDO_Terminology_Constants.Completion)) return CIDOC_CRM.E7_Activity;
		
		//lido00031 Execution  => ?
		if (lidoType.equals(LIDO_Terminology_Constants.Execution)) return CIDOC_CRM.E7_Activity;
		
		//lido00484 Expression creation  => 
		if (lidoType.equals(LIDO_Terminology_Constants.Expression_creation)) return CIDOC_CRM.E7_Activity;
		
		//lido00485 Publication Event => 
		//if (lidoType.equals(LIDO_Terminology_Constants.Publication_Event)) return CIDOC_CRM.E7_Activity;
		
		//lido00487 Carrier Production Event => ?
		if (lidoType.equals(LIDO_Terminology_Constants.Carrier_Production_Event)) return CIDOC_CRM.E7_Activity;
		
		//lido00034 Restoration  => ?
		if (lidoType.equals(LIDO_Terminology_Constants.Restoration)) return CIDOC_CRM.E7_Activity;
		
		//lido00223 Move  => E9 Move
		if (lidoType.equals(LIDO_Terminology_Constants.Move)) return CIDOC_CRM.E9_Move;
		
		//lido00030 Performance  => ?
		if (lidoType.equals(LIDO_Terminology_Constants.Performance)) return CIDOC_CRM.E7_Activity;
		
		//lido00227 Provenance  => ?
		if (lidoType.equals(LIDO_Terminology_Constants.Provenance)) return CIDOC_CRM.E7_Activity;
		
		//lido00228 Publication  => ?
		if (lidoType.equals(LIDO_Terminology_Constants.Publication)) return CIDOC_CRM.E7_Activity;
		
		//lido00005 Transfer of custody => E10 Transfer of Custody
		if (lidoType.equals(LIDO_Terminology_Constants.Transfer_of_custody)) return CIDOC_CRM.E10_Transfer_of_Custody;
		
		//lido00010 Collecting  => ?
		if (lidoType.equals(LIDO_Terminology_Constants.Collecting)) return CIDOC_CRM.E7_Activity;
		
		//lido00033 Excavation  => ?
		if (lidoType.equals(LIDO_Terminology_Constants.Excavation)) return CIDOC_CRM.E7_Activity;
		
		//lido00009 Loss  => ?
		if (lidoType.equals(LIDO_Terminology_Constants.Loss)) return CIDOC_CRM.E7_Activity;
		
		//lido00011 Use  => ?
		if (lidoType.equals(LIDO_Terminology_Constants.Use)) return CIDOC_CRM.E7_Activity;
		
		//lido00028 Beginning of Existence => E63 Beginning of Existence
		if (lidoType.equals(LIDO_Terminology_Constants.Beginning_of_Existence)) return CIDOC_CRM.E63_Beginning_of_Existence;
		
		//lido00029 Transformation  => E81 Transformation
		if (lidoType.equals(LIDO_Terminology_Constants.Transformation)) return CIDOC_CRM.E81_Transformation;
		
		//lido00027 End of Existence  => E64 End of Existence
		if (lidoType.equals(LIDO_Terminology_Constants.End_of_Existence)) return CIDOC_CRM.E64_End_of_Existence;
		
		//lido00026 Destruction  => E6 Destruction
		if (lidoType.equals(LIDO_Terminology_Constants.Destruction)) return CIDOC_CRM.E6_Destruction;
		
		//lido00029 Transformation  =>
		//if (lidoType.equals(LIDO_Terminology_Constants.Event)) return CIDOC_CRM.E5_Event;
		
		return null;
	}

}