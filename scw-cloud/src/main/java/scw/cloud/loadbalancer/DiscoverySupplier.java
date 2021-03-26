package scw.cloud.loadbalancer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import scw.cloud.DiscoveryClient;
import scw.cloud.ServiceInstance;
import scw.core.utils.CollectionUtils;

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
