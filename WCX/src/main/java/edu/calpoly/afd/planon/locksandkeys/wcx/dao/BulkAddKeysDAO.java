package edu.calpoly.afd.planon.locksandkeys.wcx.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import com.planonsoftware.tms.components.client.field.PnWebField;
import com.planonsoftware.tms.components.client.table.dto.TableDTO;
import com.planonsoftware.tms.lib.client.BusinessObject;

import edu.calpoly.afd.planon.locksandkeys.wcx.exception.WCXException;
import edu.calpoly.afd.planon.locksandkeys.wcx.settings.SettingsBulkAddKeysExtension;
import nl.planon.enterprise.service.api.IPnESBusinessObject;
import nl.planon.enterprise.service.api.PnESActionNotFoundException;
import nl.planon.enterprise.service.api.PnESBusinessException;
import nl.planon.enterprise.service.api.PnESFieldNotFoundException;
import nl.planon.zeus.clientextension.cxinterface.ICXBusinessObject;

public class BulkAddKeysDAO implements Serializable {
	private static final long serialVersionUID = 8428292668691492041L;
	protected final SettingsBulkAddKeysExtension settings;
	private PnWebField keyDefField;
    private PnWebField seqField;
    private PnWebField numKeysField;

	public BulkAddKeysDAO(SettingsBulkAddKeysExtension settings, PnWebField keyDefField, PnWebField seqField, PnWebField numKeysField) {
		this.settings = settings;
		this.keyDefField = keyDefField;
		this.seqField = seqField;
		this.numKeysField = numKeysField;
	}
	
	public void setDefaultValues(ICXBusinessObject keyDefCXBO) {
		if(!Objects.isNull(keyDefCXBO)) {
			Integer primaryKey = keyDefCXBO.getFieldByName("Syscode").getValueAsInteger();
			this.keyDefField.setValue(Arrays.asList(new TableDTO(primaryKey, keyDefCXBO.getFieldByName("Code").getValueAsString())));
		}
	    
		//this.seqField.setValue(1);
		this.numKeysField.setValue(this.settings.getAddCountDefaultVal());
	}
	
	public void validate() throws WCXException {
		if (Objects.isNull(this.keyDefField.getValue())) {
			throw new WCXException("The field '" + this.keyDefField.getLabel() + "' is required.");
		}
		if (Objects.isNull(this.seqField.getValue())) {
			throw new WCXException("The field '" + this.seqField.getLabel() + "' is required.");
		}
		if (Objects.isNull(this.numKeysField.getValue())) {
			throw new WCXException("The field '" + this.numKeysField.getLabel() + "' is required.");
		}
	}
	
	public ArrayList<String> doCreate() throws WCXException, PnESBusinessException, PnESActionNotFoundException, PnESFieldNotFoundException {
		Integer seqVal = (Integer)this.seqField.getValue();
		Integer numKeysVal = (Integer)this.numKeysField.getValue();
		int keyDefPk;
		TableDTO keyDefDTO;
		Object keyDefList = this.keyDefField.getValue();
		if(keyDefList instanceof List && !((List<?>)keyDefList).isEmpty()) {
			keyDefDTO = (TableDTO)((List<?>)keyDefList).get(0);
			keyDefPk = keyDefDTO.getPrimaryKey();
		}
		else {
			throw new WCXException("Invalid value for field '" + this.keyDefField.getLabel() + "'.");
		}
		
		ArrayList<String> resultMessages = new ArrayList<String>();
		resultMessages.add("Successfully created "+numKeysVal.toString()+" \""+keyDefDTO.toString()+"\" keys.");
		resultMessages.add("");
		resultMessages.add("Sequence numbers:");
		
		StringBuffer responseBuffer = new StringBuffer(); 
		responseBuffer.append("Sequence numbers: ");
		
		for(int i=0; i<numKeysVal; i++) {
			if(i>0) {
				responseBuffer.append(", ");
			}
			
			Integer currentSeq = seqVal + i;
			IPnESBusinessObject newBO = BusinessObject.create("Key");
			newBO.getField("KeyDefinitionRef").setValue(keyDefPk);
			newBO.getField("SequenceNumber").setValue(currentSeq);
			newBO.executeSave();
			
			responseBuffer.append(currentSeq.toString());
		}
		
		resultMessages.add(responseBuffer.toString());
		return resultMessages;
	}

}
