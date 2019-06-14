package edu.calpoly.afd.planon.locksandkeys.sx.settings;

import java.io.IOException;
import edu.calpoly.afd.planon.lib.BaseProperties;
import edu.calpoly.afd.planon.lib.exception.PropertyNotDefined;


public class SettingsKeyIssueAddReturnedBy extends BaseProperties {
	private static final long serialVersionUID = 9132704086803252525L;
	private static final String KEY_RETURNEDBYPERSONFIELD = "fields.ReturnedByPerson";
	private static final String KEY_RETURNDATEFIELD = "fields.ReturnDate";

	public SettingsKeyIssueAddReturnedBy(String parameters) throws IOException, PropertyNotDefined {
		super(parameters);
	}

	public String getReturnedByPersonField() throws PropertyNotDefined {
		return this.getStringProperty(KEY_RETURNEDBYPERSONFIELD);
	}
	public String getReturnDateField() throws PropertyNotDefined {
		return this.getStringProperty(KEY_RETURNDATEFIELD);
	}
}