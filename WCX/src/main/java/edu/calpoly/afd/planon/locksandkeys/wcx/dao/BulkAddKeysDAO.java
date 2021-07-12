package edu.calpoly.afd.planon.locksandkeys.wcx.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

import edu.calpoly.afd.planon.locksandkeys.wcx.exception.WCXException;
import edu.calpoly.afd.planon.locksandkeys.wcx.settings.SettingsBulkAddKeysWCX;
import edu.planon.lib.client.common.dto.PnFieldDTO;
import edu.planon.lib.client.common.dto.PnRecordDTO;
import edu.planon.lib.client.common.dto.PnReferenceFieldDTO;
import edu.planon.lib.esapi.ESBusinessObjectUtil;
import nl.planon.enterprise.service.api.IPnESBusinessObject;
import nl.planon.enterprise.service.api.PnESActionNotFoundException;
import nl.planon.enterprise.service.api.PnESBusinessException;
import nl.planon.enterprise.service.api.PnESFieldNotFoundException;
import nl.planon.enterprise.service.api.PnESValueType;
import nl.planon.zeus.clientextension.cxinterface.ICXBusinessObject;
import nl.planon.zeus.clientextension.cxinterface.ICXContext;

public class BulkAddKeysDAO implements Serializable {
	private static final long serialVersionUID = 8428292668691492041L;
	protected final SettingsBulkAddKeysWCX settings;
	protected final ICXContext cxContext;
	
	protected PnReferenceFieldDTO keyDefDTO;
	protected PnFieldDTO<Integer> seqDTO;
	protected PnFieldDTO<Integer> numKeysDTO;
	
	public BulkAddKeysDAO(SettingsBulkAddKeysWCX settings, ICXContext cxContext) {
		this.settings = settings;
		this.cxContext = cxContext;
		ICXBusinessObject keyDefCXBO = cxContext.getCurrentBO();
		
		this.keyDefDTO = new PnReferenceFieldDTO("keyDefPnName", settings.getKefDefLabel(), "KeyDefinition", "Code", "FreeString5");
		this.keyDefDTO.setRequired(true);
		if (!Objects.isNull(keyDefCXBO)) {
			Integer primaryKey = keyDefCXBO.getFieldByName("Syscode").getValueAsInteger();
			this.keyDefDTO.setValue(new PnRecordDTO(primaryKey, keyDefCXBO.getFieldByName("Code").getValueAsString()));
		}
		
		this.seqDTO = new PnFieldDTO<Integer>(Integer.class, "seqPnName", settings.getSeqLabel(), PnESValueType.INTEGER);
		this.seqDTO.setRequired(true);
		
		this.numKeysDTO = new PnFieldDTO<Integer>(Integer.class, "numKeysPnName", settings.getAddCountLabel(), PnESValueType.INTEGER);
		this.numKeysDTO.setRequired(true);
		this.numKeysDTO.setValue(this.settings.getAddCountDefaultVal());
	}
	
	public PnReferenceFieldDTO getKeyDefDTO() {
		return this.keyDefDTO;
	}
	
	public PnFieldDTO<Integer> getSeqDTO() {
		return this.seqDTO;
	}
	
	public PnFieldDTO<Integer> getNumKeysDTO() {
		return this.numKeysDTO;
	}
	
	public void validate() throws WCXException {
		if (Objects.isNull(this.keyDefDTO.getValue()) || this.keyDefDTO.getValue().size() != 1) {
			throw new WCXException("The field '" + this.keyDefDTO.getLabel() + "' is required.");
		}
		if (Objects.isNull(this.seqDTO.getValue())) {
			throw new WCXException("The field '" + this.seqDTO.getLabel() + "' is required.");
		}
		if (Objects.isNull(this.numKeysDTO.getValue())) {
			throw new WCXException("The field '" + this.numKeysDTO.getLabel() + "' is required.");
		}
	}
	
	public ArrayList<String> doCreate() throws WCXException, PnESBusinessException, PnESActionNotFoundException, PnESFieldNotFoundException {
		Integer seqVal = this.seqDTO.getValue();
		Integer numKeysVal = this.numKeysDTO.getValue();
		ArrayList<PnRecordDTO> keyDefValues = this.keyDefDTO.getValue();
		
		PnRecordDTO keyDefDTO = keyDefValues.get(0);
		int keyDefPk = keyDefDTO.getPrimaryKey();
		
		ArrayList<String> resultMessages = new ArrayList<String>();
		resultMessages.add("Successfully created " + numKeysVal.toString() + " \"" + keyDefDTO.getTextValue() + "\" keys.");
		resultMessages.add("");
		
		StringBuffer responseBuffer = new StringBuffer();
		responseBuffer.append("Sequence numbers: ");
		
		for (int i = 0; i < numKeysVal; i++) {
			if (i > 0) {
				responseBuffer.append(", ");
			}
			
			Integer currentSeq = seqVal + i;
			IPnESBusinessObject newBO = ESBusinessObjectUtil.create("Key");
			newBO.getField("KeyDefinitionRef").setValue(keyDefPk);
			newBO.getField("SequenceNumber").setValue(currentSeq);
			newBO.executeSave();
			
			responseBuffer.append(currentSeq.toString());
		}
		
		resultMessages.add(responseBuffer.toString());
		return resultMessages;
	}
	
}
