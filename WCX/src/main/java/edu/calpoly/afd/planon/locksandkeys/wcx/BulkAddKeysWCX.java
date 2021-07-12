package edu.calpoly.afd.planon.locksandkeys.wcx;

import java.io.IOException;

import org.apache.wicket.markup.html.panel.Panel;

import edu.calpoly.afd.planon.locksandkeys.wcx.exception.WCXException;
import edu.calpoly.afd.planon.locksandkeys.wcx.settings.SettingsBulkAddKeysWCX;
import edu.calpoly.afd.planon.locksandkeys.wcx.ui.BulkAddKeysWCXPanel;
import edu.planon.lib.client.panel.ErrorPopup;
import edu.planon.lib.common.exception.PropertyNotDefined;
import nl.planon.hera.webclientextension.wcxinterface.IViewableWebClientExtension;
import nl.planon.util.pnlogging.PnLogger;
import nl.planon.zeus.clientextension.cxinterface.ICXContext;

public class BulkAddKeysWCX implements IViewableWebClientExtension {
	private static final PnLogger LOGGER = PnLogger.getLogger(BulkAddKeysWCX.class);
	private static final String DESCRIPTION = "Creates keys in bulk.";
	private static final String DEF_TITLE = "Bulk Add Keys";
	private String title;
	private ICXContext cxContext;
	private String params;
	
	@Override
	public Panel getExtensionPanel(String panelId) {
		try {
			if (this.cxContext.getNumberOfSelectedBOs() > 1) {
				throw new WCXException("This extension does't work for multiple records.");
			}
			
			SettingsBulkAddKeysWCX settings = new SettingsBulkAddKeysWCX(this.params);
			this.title = settings.getPopupTitle(DEF_TITLE);
			
			return new BulkAddKeysWCXPanel(panelId, settings, this.cxContext);
		}
		catch (IOException | PropertyNotDefined | WCXException e) {
			LOGGER.error("Error", e);
			this.title = "Bulk Add Keys - Error";
			return new ErrorPopup(panelId, e);
		}
	}
	
	@Override
	public void refresh() {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("Refreshing WXC ");
		}
	}
	
	@Override
	public void execute(ICXContext cxContext, String aParameter) {
		this.cxContext = cxContext;
		this.params = aParameter;
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
