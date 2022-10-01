package io.basc.framework.cloud.loadbalancer;

import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

import io.basc.framework.boot.Application;
import io.basc.framework.cloud.DiscoveryClient;
import io.basc.framework.cloud.ServiceInstance;
import io.basc.framework.context.annotation.Provider;

/**
 * 轮询方案
 * 
 * @author shuchaowen
 *
 */
@Provider(value = DiscoveryLoadBalancer.class, assignableValue = false)
public class RoundRobinDiscoveryLoadBalancer implements DiscoveryLoadBalancer {
	private DiscoveryClient discoveryClient;
	private String name;
	private ConcurrentHashMap<String, LoadBalancer<ServiceInstance>> loadBalancerMap = new ConcurrentHashMap<String, LoadBalancer<ServiceInstance>>();

	public RoundRobinDiscoveryLoadBalancer(DiscoveryClient discoveryClient, Application application) {
		this(discoveryClient, application.getName());
	}

	public RoundRobinDiscoveryLoadBalancer(DiscoveryClient discoveryClient, String name) {
		this.discoveryClient = discoveryClient;
		this.name = name;
	}

	private LoadBalancer<ServiceInstance> getLoadBalancer(String name) {
		LoadBalancer<ServiceInstance> loadBalancer = loadBalancerMap.get(name);
		if (loadBalancer == null) {
			loadBalancer = new RoundRobinLoadBalancer<ServiceInstance>(new DiscoverySupplier(discoveryClient, name));
			LoadBalancer<ServiceInstance> old = loadBalancerMap.putIfAbsent(name, loadBalancer);
			if (old != null) {
				loadBalancer = old;
			}
		}
		return loadBalancer;
	}

	public Server<ServiceInstance> choose(Predicate<Server<ServiceInstance>> accept) {
		return getLoadBalancer(name).choose(accept);
	}

	public Server<ServiceInstance> choose(String name, Predicate<Server<ServiceInstance>> accept) {
		return getLoadBalancer(name).choose(accept);
	}

	public void stat(Server<ServiceInstance> server, State state) {
		for (Entry<String, LoadBalancer<ServiceInstance>> entry : loadBalancerMap.entrySet()) {
			entry.getValue().stat(server, state);
		}
	}
}
