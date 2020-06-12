package edu.calpoly.afd.planon.locksandkeys.wcx.settings;

import java.io.IOException;

import edu.planon.lib.common.BaseProperties;
import edu.planon.lib.common.exception.PropertyNotDefined;

public class SettingsBulkAddKeysExtension extends BaseProperties {
	private static final long serialVersionUID = 795518343379718128L;
	private static final String KEY_POPUP_TITLE = "popup.title";
	private static final String KEY_KEYDEF_LABEL = "keydefinition.label";
	private static final String KEY_SEQ_LABEL = "sequence.label";
	private static final String KEY_ADDCOUNT_LABEL = "addcount.label";
	private static final String KEY_ADDCOUNT_VAL = "addcount.default";
    private static final String KEY_BUTTON_OK = "btn.ok";
    private static final String KEY_BUTTON_CANCEL = "btn.cancel";
    private static final String DEFAULT_KEYDEF_LABEL = "Key definition";
    private static final String DEFAULT_SEQ_LABEL = "Starting sequence number";
    private static final String DEFAULT_ADDCOUNT_LABEL = "Number of keys to add";
    private static final int DEFAULT_ADDCOUNT_VAL = 1;
    private static final String DEFAULT_BUTTON_OK = "Ok";
    private static final String DEFAULT_BUTTON_CANCEL = "Cancel";

	public SettingsBulkAddKeysExtension(String parameters) throws IOException, PropertyNotDefined {
		super(parameters);
	}
	
	public String getPopupTitle(String defaultVal) {
        return this.getStringProperty(KEY_POPUP_TITLE, defaultVal);
    }
	
	public String getKefDefLabel() {
        return this.getStringProperty(KEY_KEYDEF_LABEL, DEFAULT_KEYDEF_LABEL);
    }
	
	public String getSeqLabel() {
        return this.getStringProperty(KEY_SEQ_LABEL, DEFAULT_SEQ_LABEL);
    }
	
	public String getAddCountLabel() {
        return this.getStringProperty(KEY_ADDCOUNT_LABEL, DEFAULT_ADDCOUNT_LABEL);
    }
	
	public int getAddCountDefaultVal() {
        return this.getIntegerProperty(KEY_ADDCOUNT_VAL, DEFAULT_ADDCOUNT_VAL);
    }

    public String getButtonOkText() {
        return this.getStringProperty(KEY_BUTTON_OK, DEFAULT_BUTTON_OK);
    }

    public String getButtonCancelText() {
        return this.getStringProperty(KEY_BUTTON_CANCEL, DEFAULT_BUTTON_CANCEL);
    }
}
