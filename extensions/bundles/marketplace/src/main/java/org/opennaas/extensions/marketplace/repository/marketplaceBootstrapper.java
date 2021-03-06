package org.opennaas.extensions.marketplace.repository;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.IModel;
import org.opennaas.core.resources.IResourceBootstrapper;
import org.opennaas.core.resources.Resource;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.extensions.marketplace.model.SampleModel;

/**
 * 
 * @author Elisabeth Rigol
 * 
 */
public class marketplaceBootstrapper implements IResourceBootstrapper {

	Log				log	= LogFactory.getLog(marketplaceBootstrapper.class);

	private IModel	oldModel;

	public void bootstrap(Resource resource) throws ResourceException {

		log.info("Loading bootstrap to start resource...");
		resource.setModel(new SampleModel());
		// Add here all the necessary methods to populate resource model
		//
	}

	@Override
	public void resetModel(Resource resource) throws ResourceException {

		resource.setModel(new SampleModel());
	}

	@Override
	public void revertBootstrap(Resource resource) throws ResourceException {

		resource.setModel(oldModel);
	}

}
