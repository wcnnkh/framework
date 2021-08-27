package io.basc.framework.cloud.loadbalancer;

import io.basc.framework.cloud.ServiceInstance;

public interface DiscoveryLoadBalancer extends LoadBalancer<ServiceInstance>{
	Server<ServiceInstance> choose(String name, ServerAccept<ServiceInstance> accept);
}
