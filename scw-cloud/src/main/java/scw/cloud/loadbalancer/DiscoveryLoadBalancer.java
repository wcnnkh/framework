package scw.cloud.loadbalancer;

import scw.cloud.ServiceInstance;

public interface DiscoveryLoadBalancer extends LoadBalancer<ServiceInstance>{
	Server<ServiceInstance> choose(String name, ServerAccept<ServiceInstance> accept);
}
