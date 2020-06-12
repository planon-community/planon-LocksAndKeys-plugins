package edu.calpoly.afd.planon.locksandkeys.wcx.ui;

import java.util.ArrayList;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.CssResourceReference;

import com.planonsoftware.tms.components.client.common.action.ButtonCloseListener;
import com.planonsoftware.tms.components.client.common.action.IWebActionListener;
import com.planonsoftware.tms.components.client.common.dto.FieldDTO;
import com.planonsoftware.tms.components.client.common.dto.ReferenceFieldDTO;
import com.planonsoftware.tms.components.client.common.ui.PnAjaxEventBehavior;
import com.planonsoftware.tms.components.client.common.ui.WebActionEvent;
import com.planonsoftware.tms.components.client.field.PnWebField;
import com.planonsoftware.tms.components.client.popup.PnErrorPopup;
import com.planonsoftware.tms.components.client.popup.PnInfoPopup;
import edu.calpoly.afd.planon.locksandkeys.wcx.dao.BulkAddKeysDAO;
import edu.calpoly.afd.planon.locksandkeys.wcx.exception.WCXException;
import edu.calpoly.afd.planon.locksandkeys.wcx.settings.SettingsBulkAddKeysExtension;
import nl.planon.enterprise.service.api.PnESActionNotFoundException;
import nl.planon.enterprise.service.api.PnESBusinessException;
import nl.planon.enterprise.service.api.PnESFieldNotFoundException;
import nl.planon.enterprise.service.api.PnESValueType;
import nl.planon.zeus.clientextension.cxinterface.*;

public class BulkAddKeysExtensionPanel extends Panel {
	private static final long serialVersionUID = -3046887001844940438L;
    private static final String CSS_FILE = "css/extension-styles.css";
	protected final SettingsBulkAddKeysExtension settings;
    private final ModalWindow resultWindow = new ModalWindow("popupPanel");
    private boolean errorFlag = true;
    private PnWebField keyDefField;
    private PnWebField seqField;
    private PnWebField numKeysField;
    private BulkAddKeysDAO dao;

	public BulkAddKeysExtensionPanel(String panelId, SettingsBulkAddKeysExtension settings, ICXContext cxContext) throws PnESBusinessException, PnESActionNotFoundException, PnESFieldNotFoundException {
		super(panelId);
		this.settings = settings;
		
		ReferenceFieldDTO referenceFieldDTO = new ReferenceFieldDTO("KeyDefinition", "Code");
		FieldDTO keyDefDTO = new FieldDTO(settings.getKefDefLabel(), "keyDefPnName", PnESValueType.REFERENCE, referenceFieldDTO);
		keyDefDTO.setMandatory(true);
	    this.keyDefField = new PnWebField("keyDefField", keyDefDTO);
		
		FieldDTO seqDTO = new FieldDTO(settings.getSeqLabel(), "numKeysPnName", PnESValueType.INTEGER);
		seqDTO.setMandatory(true);
	    this.seqField = new PnWebField("seqField", seqDTO);
		
		FieldDTO numKeysDTO = new FieldDTO(settings.getAddCountLabel(), "numKeysPnName", PnESValueType.INTEGER);
		numKeysDTO.setMandatory(true);
	    this.numKeysField = new PnWebField("numKeysField", numKeysDTO);
	    
	    
	    this.dao = new BulkAddKeysDAO(settings, keyDefField, seqField, numKeysField);
	    this.dao.setDefaultValues(cxContext.getCurrentBO());
	    
	    this.add(new Component[] { this.keyDefField });
	    this.add(new Component[] { this.seqField });
	    this.add(new Component[] { this.numKeysField });

        this.initResultWindow();
        this.initButtons(settings);
	}
	
	private void initButtons(SettingsBulkAddKeysExtension settings) {
		Button okButton = new Button("btnOk", Model.of(settings.getButtonOkText()));
		PnAjaxEventBehavior okButtonBehavior = new PnAjaxEventBehavior("click");
		okButton.add(okButtonBehavior);
		okButtonBehavior.addWebActionListener(new ButtonOkListener());
		this.add(okButton);
		
		Button btnCancel = new Button("btnCancel", Model.of(settings.getButtonCancelText()));
		PnAjaxEventBehavior closeButtonBehavior = new PnAjaxEventBehavior("click");
		btnCancel.add(closeButtonBehavior);
		closeButtonBehavior.addWebActionListener(new ButtonCloseListener());
		this.add(btnCancel);
    }
	
	private void initResultWindow() {
		this.resultWindow.setInitialWidth(400);
		this.resultWindow.setInitialHeight(250);
		this.resultWindow.setCloseButtonCallback(new ModalWindow.CloseButtonCallback() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public boolean onCloseButtonClicked(AjaxRequestTarget target) {
				ModalWindow.closeCurrent(target);
				if (!BulkAddKeysExtensionPanel.this.isErrorFlag()) {
					ModalWindow.closeCurrent(target);
					ModalWindow.closeCurrent(target);
					return true;
				}
				return false;
			}
		});
		this.add(this.resultWindow);
		this.resultWindow.setOutputMarkupId(true);
    }
	
    public boolean isErrorFlag() {
    	return this.errorFlag;
	}
    
    public void setErrorFlag(boolean aErrorFlag) {
    	this.errorFlag = aErrorFlag;
	}

    @Override
    public final void renderHead(IHeaderResponse response) {
        response.render(CssHeaderItem.forReference(new CssResourceReference(BulkAddKeysExtensionPanel.class, CSS_FILE)));
    }
    
	protected void showError(WebActionEvent aWebActionEvent, Exception exception) {
		this.resultWindow.setContent(new PnErrorPopup(this.resultWindow.getContentId(), exception));
		this.resultWindow.setTitle("Error");
		this.resultWindow.show(aWebActionEvent.getTarget());
    }
	
	class ButtonOkListener implements IWebActionListener {
		private static final long serialVersionUID = -7715293873838420496L;

		ButtonOkListener() {
        }

        @Override
        public void webActionPerformed(WebActionEvent aWebActionEvent) {
            try {
            	BulkAddKeysExtensionPanel.this.dao.validate();
            	ArrayList<String> resultMessages = BulkAddKeysExtensionPanel.this.dao.doCreate();
            	BulkAddKeysExtensionPanel.this.setErrorFlag(false);
            	PnInfoPopup pnInfoPopup = new PnInfoPopup(BulkAddKeysExtensionPanel.this.resultWindow.getContentId(), resultMessages);              
                pnInfoPopup.addCloseWebActionListener(new ButtonCloseListener());
                BulkAddKeysExtensionPanel.this.resultWindow.setContent(pnInfoPopup);
                
                BulkAddKeysExtensionPanel.this.resultWindow.setTitle("Success");
                BulkAddKeysExtensionPanel.this.resultWindow.show(aWebActionEvent.getTarget());
                //BulkAddKeysExtensionPanel.this.cxContext.refreshBOGrid();
            }
            catch (WCXException | PnESBusinessException | PnESActionNotFoundException | PnESFieldNotFoundException e) {
            	BulkAddKeysExtensionPanel.this.showError(aWebActionEvent, e);
            }
        }
    }
}
