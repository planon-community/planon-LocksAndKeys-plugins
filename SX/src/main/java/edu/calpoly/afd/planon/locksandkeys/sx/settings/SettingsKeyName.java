package edu.calpoly.afd.planon.locksandkeys.sx.settings;

import java.io.IOException;

import edu.calpoly.afd.planon.lib.BaseProperties;
import edu.calpoly.afd.planon.lib.exception.PropertyNotDefined;

public class SettingsKeyName extends BaseProperties {
	private static final long serialVersionUID = 1L;
	private static final String KEY_DESCRIPTIONFIELD = "fields.KeyDescription";
	
	public SettingsKeyName(String parameters) throws IOException {
		super(parameters);
	}
	
	public String getKeyDescriptionField() throws PropertyNotDefined {
		return this.getStringProperty(KEY_DESCRIPTIONFIELD);
	}

}
