package io.basc.framework.cloud.loadbalancer;

import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

import io.basc.framework.boot.Application;
import io.basc.framework.cloud.DiscoveryClient;
import io.basc.framework.cloud.ServiceInstance;
import io.basc.framework.context.annotation.Provider;
import io.basc.framework.util.Assert;
import io.basc.framework.util.Optional;
import io.basc.framework.util.Selector;

/**
 * 轮询方案
 * 
 * @author wcnnkh
 *
 */
@Provider(value = DiscoveryLoadBalancer.class, assignableValue = false)
public class DefaultDiscoveryLoadBalancer implements DiscoveryLoadBalancer {
	private final DiscoveryClient discoveryClient;
	private final Optional<String> name;
	private ConcurrentHashMap<String, LoadBalancer<ServiceInstance>> loadBalancerMap = new ConcurrentHashMap<String, LoadBalancer<ServiceInstance>>();
	private final Selector<Server<ServiceInstance>> selector;

	public DefaultDiscoveryLoadBalancer(DiscoveryClient discoveryClient, Application application) {
		// 默认使用轮询
		this(discoveryClient, application, Selector.roundRobin());
	}

	public DefaultDiscoveryLoadBalancer(DiscoveryClient discoveryClient, Application application,
			Selector<Server<ServiceInstance>> selector) {
		this(discoveryClient, application.getName(), selector);
	}

	public DefaultDiscoveryLoadBalancer(DiscoveryClient discoveryClient, Optional<String> name,
			Selector<Server<ServiceInstance>> selector) {
		Assert.requiredArgument(discoveryClient != null, "discoveryClient");
		Assert.requiredArgument(name != null, "name");
		Assert.requiredArgument(selector != null, "selector");
		this.discoveryClient = discoveryClient;
		this.name = name;
		this.selector = selector;
	}

	public LoadBalancer<ServiceInstance> getLoadBalancer(String name) {
		LoadBalancer<ServiceInstance> loadBalancer = loadBalancerMap.get(name);
		if (loadBalancer == null) {
			loadBalancer = new DefaultLoadBalancer<ServiceInstance>(new DiscoverySupplier(discoveryClient, name),
					selector);
			LoadBalancer<ServiceInstance> old = loadBalancerMap.putIfAbsent(name, loadBalancer);
			if (old != null) {
				loadBalancer = old;
			}
		}
		return loadBalancer;
	}

	public Server<ServiceInstance> choose(Predicate<Server<ServiceInstance>> accept) {
		if (!name.isPresent()) {
			return null;
		}
		return choose(name.get(), accept);
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
