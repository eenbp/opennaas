package org.opennaas.extensions.marketplace.capability.mapping;

import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapability;

/**
 * 
 * @author Elisabeth Rigol
 * 
 */
public interface IMappingCapability extends ICapability {

	/**
	 * @return
	 * @throws CapabilityException
	 */
	public String mapVN(String VNRequest) throws CapabilityException;

}
