package edu.calpoly.afd.planon.locksandkeys.sx;

import java.io.IOException;

import edu.calpoly.afd.planon.lib.BaseSX;
import edu.calpoly.afd.planon.lib.exception.PropertyNotDefined;
import edu.calpoly.afd.planon.lib.exception.SXException;
import edu.calpoly.afd.planon.locksandkeys.sx.settings.SettingsKeyName;
import nl.planon.hades.userextension.uxinterface.*;

public class KeyNameSX extends BaseSX {
	private static final String DESCRIPTION = "KeyNameSX--This SX copies the KeyDefinition & Sequence Number into the Key.Name field.";
	
	public KeyNameSX() {
		super(DESCRIPTION);
	}

	@Override
	protected void execute(IUXBusinessObject newBO, IUXBusinessObject oldBO, IUXContext context, String parameters)
			throws PropertyNotDefined, SXException, IOException {
		SettingsKeyName settings = new SettingsKeyName(parameters);
		
		String keyDescriptionFieldName = settings.getKeyDescriptionField();
		
		//Check for valid BO types
		this.checkBOType(newBO, "Key", "KeyDefinition");
		
		if(newBO.getTypeName() == "Key") {
			IUXIntegerField seqNumField = newBO.getIntegerFieldByName("SequenceNumber");
			IUXReferenceField keyDefRefField = newBO.getReferenceFieldByName("KeyDefinitionRef");
			IUXStringField keyDefCodeField = keyDefRefField.getValueAsBO().getStringFieldByName("Code");
			IUXStringField keyDescriptionField = newBO.getStringFieldByName(keyDescriptionFieldName);
			
			if(oldBO == null || seqNumField.isChanged() || keyDefRefField.isChanged()) {
				keyDescriptionField.setValueAsString(keyDefCodeField.getValueAsString() + ", #" + seqNumField.getValueAsString());	
			}
		}
		
		if(newBO.getTypeName() == "KeyDefinition") {
			IUXStringField codeField = newBO.getStringFieldByName("Code");
			String keyDefStr = codeField.getValueAsString();
			
			if(codeField.isChanged()) {
				IUXAssociation keys = newBO.getAssociationByName("Key|KeyDefinitionRef");
				
				keys.forEach((key) -> {
					if(!key.isArchived()) {
						IUXIntegerField seqNumField = key.getIntegerFieldByName("SequenceNumber");
						IUXStringField keyDescriptionField = key.getStringFieldByName(keyDescriptionFieldName);
						
						keyDescriptionField.setValueAsString(keyDefStr + ", #" + seqNumField.getValueAsString());
						key.save();
					}
				});
			}
			
			
		}

	}

}
