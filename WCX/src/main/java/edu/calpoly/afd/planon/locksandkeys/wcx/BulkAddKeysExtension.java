package edu.calpoly.afd.planon.locksandkeys.wcx;

import java.io.IOException;

import org.apache.wicket.markup.html.panel.Panel;

import com.planonsoftware.tms.components.client.popup.PnErrorPopup;

import edu.calpoly.afd.planon.locksandkeys.wcx.exception.WCXException;
import edu.calpoly.afd.planon.locksandkeys.wcx.settings.SettingsBulkAddKeysExtension;
import edu.calpoly.afd.planon.locksandkeys.wcx.ui.BulkAddKeysExtensionPanel;
import edu.planon.lib.common.exception.PropertyNotDefined;
import nl.planon.enterprise.service.api.PnESActionNotFoundException;
import nl.planon.enterprise.service.api.PnESBusinessException;
import nl.planon.enterprise.service.api.PnESFieldNotFoundException;
import nl.planon.hera.webclientextension.wcxinterface.IViewableWebClientExtension;
import nl.planon.zeus.clientextension.cxinterface.ICXContext;

public class BulkAddKeysExtension implements IViewableWebClientExtension {
	private static final String DESCRIPTION = "Creates keys in bulk.";
    private static final String DEF_TITLE = "Bulk Add Keys";
    private static final String ERROR_TITLE = "Bulk Add Keys - Error";
    private String title;
    private ICXContext cxContext;
    private String params;

	@Override
	public void execute(ICXContext context, String aParams) {
		this.cxContext = context;
		this.params = aParams;
	}
	
	@Override
	public Panel getExtensionPanel(String panelId) {
		try {
			if(this.cxContext.getNumberOfSelectedBOs() > 1) {
                throw new WCXException("This extension does't work for multiple records.");
            }
			
			SettingsBulkAddKeysExtension settings = new SettingsBulkAddKeysExtension(this.params);
			this.title = settings.getPopupTitle(DEF_TITLE);
			
			return new BulkAddKeysExtensionPanel(panelId, settings, this.cxContext);
		} catch (IOException | PropertyNotDefined | WCXException | PnESBusinessException | PnESActionNotFoundException | PnESFieldNotFoundException e) {
			this.title = ERROR_TITLE;
			return new PnErrorPopup(panelId, "Close", e);
		}
	}
	
	@Override
	public void refresh() {
		// TODO Auto-generated method stub

	}

	@Override
	public String getTitle() {
		return this.title;
	}

	@Override
	public String getDescription() {
		return DESCRIPTION;
	}
	
}
