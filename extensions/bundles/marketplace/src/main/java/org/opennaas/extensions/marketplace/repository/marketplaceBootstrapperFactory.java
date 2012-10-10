package org.opennaas.extensions.marketplace.repository;

import org.opennaas.core.resources.IResourceBootstrapper;
import org.opennaas.core.resources.IResourceBootstrapperFactory;

/**
 * 
 * @author Elisabeth Rigol
 * 
 */
public class marketplaceBootstrapperFactory implements IResourceBootstrapperFactory {

	@Override
	public IResourceBootstrapper createResourceBootstrapper() {

		return new marketplaceBootstrapper();
	}

}
