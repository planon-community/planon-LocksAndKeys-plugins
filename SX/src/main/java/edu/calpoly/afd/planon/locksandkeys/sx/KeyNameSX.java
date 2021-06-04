package edu.calpoly.afd.planon.locksandkeys.sx;

import java.io.IOException;
import edu.planon.lib.sx.BaseSX;
import edu.planon.lib.sx.exception.SXException;
import edu.planon.lib.common.exception.PropertyNotDefined;
import nl.planon.hades.userextension.uxinterface.*;

public class KeyNameSX extends BaseSX {
	private static final String DESCRIPTION = "KeyNameSX--This SX copies the KeyDefinition into the Key.Name field and the SequenceNumber into the Code field.";
	
	public KeyNameSX() {
		super(DESCRIPTION);
	}

	@Override
	protected void execute(IUXBusinessObject newBO, IUXBusinessObject oldBO, IUXContext context, String parameters)
			throws PropertyNotDefined, SXException, IOException {
		//Check for valid BO types
		this.checkBOType(newBO, "Key", "KeyDefinition");
		
		//If Key
		if(newBO.getTypeName() == "Key") {
			IUXIntegerField seqNumField = newBO.getIntegerFieldByName("SequenceNumber");
			IUXReferenceField keyDefRefField = newBO.getReferenceFieldByName("KeyDefinitionRef");
			IUXStringField keyDefCodeField = keyDefRefField.getValueAsBO().getStringFieldByName("Code");
			String keyDefCode = keyDefCodeField.getValueAsString();
			
			IUXStringField codeField = newBO.getStringFieldByName("Code");
			IUXStringField keyNameField = newBO.getStringFieldByName("Name");
			
			//Update Code
			if(oldBO == null || seqNumField.isChanged() ) {
				//if we don't have a seqNumField we have to calculate it ourselves 
				if(seqNumField.isEmpty()) {
					Integer seqNum = 1;
					
					IUXDatabaseQueryBuilder qBldr = context.getBODatabaseQueryBuilder("Key");
					qBldr.addSelectField("SequenceNumber");
					qBldr.addSearchField("KeyDefinitionRef");
					qBldr.addOrderByDef("SequenceNumber", false);
					IUXDatabaseQuery dbQuery = qBldr.build();
					dbQuery.getReferenceSearchExpression("KeyDefinitionRef", UXOperator.EQUAL).setValueByLookup(keyDefCode);
					IUXResultSet resultSet = dbQuery.executeAll();
					if(resultSet.first()) {
						seqNum = resultSet.getInteger("SequenceNumber") + 1;
					}
					
					codeField.setValueAsString(seqNum.toString());
					seqNumField.setValueAsInteger(seqNum);
				}
				else {
					codeField.setValueAsString(seqNumField.getValueAsString());
				}
			}
			
			//Update Name
			if(oldBO == null || keyDefRefField.isChanged()) {
				int maxKeyNameLen = keyNameField.getFieldDefinition().getInputLength();
				keyNameField.setValueAsString(keyDefCode.substring(0, Math.min(keyDefCode.length(), maxKeyNameLen)));
			}
		}
		
		//If KeyDefinition
		if(newBO.getTypeName() == "KeyDefinition") {
			IUXStringField keyDefCodeField = newBO.getStringFieldByName("Code");
			String keyDefCode = keyDefCodeField.getValueAsString();
			
			if(keyDefCodeField.isChanged()) {
				IUXAssociation keys = newBO.getAssociationByName("Key|KeyDefinitionRef");
				
				keys.forEach((key) -> {
					if(!key.isArchived()) {
						IUXStringField keyNameField = key.getStringFieldByName("Name");
						int maxKeyNameLen = keyNameField.getFieldDefinition().getInputLength();
						
						keyNameField.setValueAsString(keyDefCode.substring(0, Math.min(keyDefCode.length(), maxKeyNameLen)));
						key.save();
					}
				});
			}
		}
	}

}
