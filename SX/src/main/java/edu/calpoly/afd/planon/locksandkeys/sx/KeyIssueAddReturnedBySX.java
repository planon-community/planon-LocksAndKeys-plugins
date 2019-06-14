package edu.calpoly.afd.planon.locksandkeys.sx;

import java.io.IOException;
import edu.calpoly.afd.planon.lib.BaseSX;
import edu.calpoly.afd.planon.lib.exception.PropertyNotDefined;
import edu.calpoly.afd.planon.lib.exception.SXException;
import edu.calpoly.afd.planon.locksandkeys.sx.settings.SettingsKeyIssueAddReturnedBy;
import nl.planon.hades.userextension.uxinterface.*;



public class KeyIssueAddReturnedBySX extends BaseSX {
	private static final String DESCRIPTION = "KeyIssueAddReturnedBy--This SX will add a reference to the person that executes a key/keyset return.";
	
	public KeyIssueAddReturnedBySX() {
		super(DESCRIPTION);
	}
	
	public void execute(IUXBusinessObject newBO, IUXBusinessObject oldBO, IUXContext context, String parameters) throws PropertyNotDefined, SXException, IOException {
		SettingsKeyIssueAddReturnedBy settings = new SettingsKeyIssueAddReturnedBy(parameters);
			
		String returnedByPersonFieldName = settings.getReturnedByPersonField();
		String returnDateFieldName = settings.getReturnDateField();
		
		//Check for valid BO types
		this.checkBOType(newBO, "KeySetIssue", "KeyIssue");
		
		//If returnDateFieldName was empty but not now
		if(oldBO.getFieldByName(returnDateFieldName).isEmpty() && !newBO.getFieldByName(returnDateFieldName).isEmpty()) {
			IUXReferenceField returnByRef = newBO.getReferenceFieldByName(returnedByPersonFieldName);
			
			IUXBusinessObject person = context.getUserAsPerson();
			
			returnByRef.setValueAsBO(person);
		}
		//If returnDateFieldName cleared out
		else if(!oldBO.getFieldByName(returnDateFieldName).isEmpty() && newBO.getFieldByName(returnDateFieldName).isEmpty()) {
			IUXReferenceField returnByRef = newBO.getReferenceFieldByName(returnedByPersonFieldName);
			
			returnByRef.clear();
		}
	}
}
