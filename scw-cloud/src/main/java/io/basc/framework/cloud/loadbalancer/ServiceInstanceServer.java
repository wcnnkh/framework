package io.basc.framework.cloud.loadbalancer;

import io.basc.framework.cloud.ServiceInstance;

public class ServiceInstanceServer implements Server<ServiceInstance>{
	private final ServiceInstance serviceInstance;
	private final int weight;
	
	public ServiceInstanceServer(ServiceInstance serviceInstance, int weight){
		this.serviceInstance = serviceInstance;
		this.weight = weight;
	}
	
	public String getId() {
		return serviceInstance.getName() + ":" + serviceInstance.getId();
	}

	public int getWeight() {
		return weight;
	}

	public ServiceInstance getService() {
		return serviceInstance;
	}

}
