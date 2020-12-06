package scw.loadbalancer;

import scw.discovery.ServiceInstance;

public interface DiscoveryLoadBalancer extends LoadBalancer<ServiceInstance>{
	Server<ServiceInstance> choose(String name, ServerAccept<ServiceInstance> accept);
}
