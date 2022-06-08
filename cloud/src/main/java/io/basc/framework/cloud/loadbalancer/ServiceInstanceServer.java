package io.basc.framework.cloud.loadbalancer;

import io.basc.framework.cloud.ServiceInstance;
import io.basc.framework.core.reflect.ReflectionUtils;

public class ServiceInstanceServer implements Server<ServiceInstance> {
	private final ServiceInstance serviceInstance;
	private final int weight;

	public ServiceInstanceServer(ServiceInstance serviceInstance, int weight) {
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

	@Override
	public String toString() {
		return ReflectionUtils.toString(this);
	}
}
