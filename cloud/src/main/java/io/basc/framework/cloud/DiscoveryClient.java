package io.basc.framework.cloud;

import java.util.List;

public interface DiscoveryClient {

	List<ServiceInstance> getInstances(String name) throws DiscoveryClientException;

	List<String> getServices() throws DiscoveryClientException;
}
