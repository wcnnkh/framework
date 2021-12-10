package io.basc.framework.cloud;

import java.util.List;

public interface DiscoveryClient {

	/**
	 * Gets all ServiceInstances associated with a particular name.
	 * 
	 * @param serviceId The serviceId to query.
	 * @return A List of ServiceInstance.
	 */
	List<ServiceInstance> getInstances(String name) throws DiscoveryClientException;

	/**
	 * @return All known service names.
	 */
	List<String> getServices() throws DiscoveryClientException;
}
