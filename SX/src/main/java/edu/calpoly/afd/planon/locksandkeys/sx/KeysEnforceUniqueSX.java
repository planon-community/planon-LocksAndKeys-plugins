package edu.calpoly.afd.planon.locksandkeys.sx;

import java.io.IOException;

import edu.calpoly.afd.planon.lib.BaseSX;
import edu.calpoly.afd.planon.lib.exception.PropertyNotDefined;
import edu.calpoly.afd.planon.lib.exception.SXException;
import nl.planon.hades.userextension.uxinterface.*;

public class KeysEnforceUniqueSX extends BaseSX {
	private static final String DESCRIPTION = "KeysEnforceUniqueSX--This SX will enforce a unique combination of KeyDefinitionRef & SequenceNumber.";
	
	public KeysEnforceUniqueSX() {
		super(DESCRIPTION);
	}

	public void execute(IUXBusinessObject newBO, IUXBusinessObject oldBO, IUXContext context, String parameters) throws PropertyNotDefined, SXException, IOException {
		IUXIntegerField newSeqNumFd = newBO.getIntegerFieldByName("SequenceNumber");
		
		//Check for valid BO types
		this.checkBOType(newBO, "Key");
				
		//only look if the SequenceNumber changed and not empty
    	if(newSeqNumFd.isChanged() && !newSeqNumFd.isEmpty()) {
    		Integer newSeqNum = newSeqNumFd.getValueAsInteger();
    		Integer newPk = newBO.getPrimaryKey();
			IUXReferenceField newKeyDefRef = newBO.getReferenceFieldByName("KeyDefinitionRef");
			
		    if (!newKeyDefRef.isEmpty()) {
		    	IUXBusinessObject newKeyDefBO = newKeyDefRef.getValueAsBO();
		    	IUXStringField newKeyDefCodeFd = newKeyDefBO.getStringFieldByName("Code");
		    	String newKeyDefCode = newKeyDefCodeFd.getValueAsString();
		    	
		    	IUXDatabaseQueryBuilder queryBldr = context.getBODatabaseQueryBuilder(newBO.getBOType());
		    	queryBldr.addSelectField("Syscode");
		    	queryBldr.setQueryArchive(); //Include Archives
		    	queryBldr.addSearchField("SequenceNumber");
		    	queryBldr.addSearchField("KeyDefinitionRef");
		    	queryBldr.addSearchField("Syscode");
		    	
		    	IUXDatabaseQuery query = queryBldr.build();
		    	
		    	query.getIntegerSearchExpression("SequenceNumber", UXOperator.EQUAL).setValue(newSeqNum);
		    	query.getReferenceSearchExpression("KeyDefinitionRef", UXOperator.EQUAL).setValueByLookup(newKeyDefCode);
		    	query.getIntegerSearchExpression("Syscode", UXOperator.NOT_EQUAL).setValue(newPk);
		    	
		    	if(query.executeAll().first()) {
		    		context.addError(909, "The SequenceNumber can't be the same as another key of the same KeyDefinition.");
		    	}
	    	}
	    }
	    
	    if(oldBO != null) {
	    	
	    }
	}
}

