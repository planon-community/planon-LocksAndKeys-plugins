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
			
			//Update Code
			if(oldBO == null || seqNumField.isChanged() ) {
				newBO.getStringFieldByName("Code").setValueAsString(seqNumField.getValueAsString());
			}
			
			//Update Name
			if(oldBO == null || keyDefRefField.isChanged()) {
				IUXStringField keyNameField = newBO.getStringFieldByName("Name");
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
