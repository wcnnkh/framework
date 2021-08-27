package io.basc.framework.cloud.loadbalancer;

import io.basc.framework.cloud.DiscoveryClient;
import io.basc.framework.cloud.ServiceInstance;
import io.basc.framework.core.utils.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DiscoverySupplier implements ServerSupplier<ServiceInstance>{
	private final DiscoveryClient discoveryClient;
	private final String name;
	
	public DiscoverySupplier(DiscoveryClient discoveryClient, String name){
		this.discoveryClient = discoveryClient;
		this.name = name;
	}
	
	public List<Server<ServiceInstance>> getServers() {
		List<ServiceInstance> instances = discoveryClient.getInstances(name);
		if(CollectionUtils.isEmpty(instances)){
			return Collections.emptyList();
		}
		
		List<Server<ServiceInstance>> servers = new ArrayList<Server<ServiceInstance>>(instances.size());
		for(ServiceInstance instance : instances){
			servers.add(new ServiceInstanceServer(instance, 1));
		}
		
		return servers;
	}
}
