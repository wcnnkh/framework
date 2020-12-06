package scw.loadbalancer;

import scw.discovery.ServiceInstance;

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
