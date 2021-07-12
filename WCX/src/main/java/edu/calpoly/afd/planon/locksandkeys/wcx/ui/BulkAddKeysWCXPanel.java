package edu.calpoly.afd.planon.locksandkeys.wcx.ui;

import java.util.ArrayList;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.IMarkupCacheKeyProvider;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.CssResourceReference;

import edu.calpoly.afd.planon.locksandkeys.wcx.dao.BulkAddKeysDAO;
import edu.calpoly.afd.planon.locksandkeys.wcx.exception.WCXException;
import edu.calpoly.afd.planon.locksandkeys.wcx.settings.SettingsBulkAddKeysWCX;
import edu.planon.lib.client.common.behavior.CloseModalBehavior;
import edu.planon.lib.client.field.PnFieldPanel;
import edu.planon.lib.client.panel.AbstractPanel;
import edu.planon.lib.client.panel.InfoPopup;
import nl.planon.enterprise.service.api.PnESActionNotFoundException;
import nl.planon.enterprise.service.api.PnESBusinessException;
import nl.planon.enterprise.service.api.PnESFieldNotFoundException;
import nl.planon.util.pnlogging.PnLogger;
import nl.planon.zeus.clientextension.cxinterface.ICXContext;

public class BulkAddKeysWCXPanel extends AbstractPanel implements IMarkupCacheKeyProvider {
	private static final long serialVersionUID = -1L;
	private static final PnLogger LOGGER = PnLogger.getLogger(BulkAddKeysWCXPanel.class);
	private static final String CSS_FILE = "css/extension-styles.css";
	protected final SettingsBulkAddKeysWCX settings;
	private BulkAddKeysDAO dao;
	
	public BulkAddKeysWCXPanel(String id, SettingsBulkAddKeysWCX settings, ICXContext cxContext) {
		super(id);
		this.settings = settings;
		this.dao = new BulkAddKeysDAO(settings, cxContext);
		
		this.add(new PnFieldPanel("keyDefField", this.dao.getKeyDefDTO()));
		this.add(new PnFieldPanel("seqField", this.dao.getSeqDTO()));
		this.add(new PnFieldPanel("numKeysField", this.dao.getNumKeysDTO()));
		
		this.initBaseButtonPanel(settings);
	}
	
	private void okActionHandler(AjaxRequestTarget target) {
		try {
			this.dao.validate();
			ArrayList<String> resultMessages = this.dao.doCreate();
			
			this.setClosePopup(true);
			ModalWindow popupWindow = this.getPopupWindow();
			popupWindow.setContent(new InfoPopup(popupWindow.getContentId(), resultMessages));
			popupWindow.setTitle("Success");
			popupWindow.show(target);
			this.setClosePopup(true);
			
			//this.cxContext.refreshBOGrid();
		}
		catch (WCXException | PnESBusinessException | PnESActionNotFoundException | PnESFieldNotFoundException e) {
			LOGGER.error("Error in okActionHandler", e);
			this.showError(target, e);
		}
	}
	
	private void initBaseButtonPanel(SettingsBulkAddKeysWCX settings) {
		// get labels from settings 
		String okBtnText = settings.getButtonOkText();
		String cancelBtnText = settings.getButtonCancelText();
		
		Button btnOk = new Button("btnOk", Model.of(okBtnText));
		btnOk.add(new AjaxEventBehavior("click") {
			private static final long serialVersionUID = 1L;
			
			@Override
			protected void onEvent(AjaxRequestTarget target) {
				BulkAddKeysWCXPanel.this.okActionHandler(target);
			}
		});
		btnOk.setOutputMarkupPlaceholderTag(true);
		this.add(btnOk);
		
		Button btnCancel = new Button("btnCancel", Model.of(cancelBtnText));
		btnCancel.add(new CloseModalBehavior("click"));
		this.add(btnCancel);
	}
	
	@Override
	public final void renderHead(IHeaderResponse response) {
		response.render(CssHeaderItem.forReference(new CssResourceReference(BulkAddKeysWCXPanel.class, CSS_FILE)));
	}
	
	// Avoid markup caching for this component
	@Override
	public String getCacheKey(MarkupContainer arg0, Class<?> arg1) {
		return null;
	}
}
