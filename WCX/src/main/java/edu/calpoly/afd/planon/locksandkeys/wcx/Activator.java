package edu.calpoly.afd.planon.locksandkeys.wcx;

import java.util.*;
import org.osgi.framework.*;

public class Activator implements BundleActivator {
	@SuppressWarnings("rawtypes")
	private final List<ServiceRegistration> serviceRegisterList = new ArrayList<>();
	
	@Override
	public void start(BundleContext aContext) {
		// Register the Client Extension as a service . The right interface name has to be used here, because
		// Planon Application retrieves the service using this interface name. This name also has
		// to be given when the CUX gets configured in the field definer against a Business Object
		// in the Planon Application.
		
		this.serviceRegisterList.add(aContext.registerService(BulkAddKeysExtension.class.getName(), new BulkAddKeysExtension(), null));
	}
		
	
	@SuppressWarnings("rawtypes")
	@Override
	public void stop(BundleContext aContext) {
		for (ServiceRegistration registration : this.serviceRegisterList) {
			registration.unregister();
		}
		this.serviceRegisterList.clear();
	}
}
