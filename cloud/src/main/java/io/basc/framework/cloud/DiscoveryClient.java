package io.basc.framework.cloud;

import java.util.List;

public interface DiscoveryClient {

	List<Service> getInstances(String name) throws DiscoveryClientException;

	List<String> getServices() throws DiscoveryClientException;
}
