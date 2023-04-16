package io.basc.framework.cloud.loadbalancer;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.basc.framework.cloud.DiscoveryClient;
import io.basc.framework.cloud.Service;
import io.basc.framework.context.annotation.Provider;
import io.basc.framework.util.Assert;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.Elements;
import io.basc.framework.util.Selector;

@Provider(value = DiscoveryLoadBalancer.class, assignableValue = false)
public class DefaultDiscoveryLoadBalancer extends AbstractLoadBalancer<Service> implements DiscoveryLoadBalancer {
	private final DiscoveryClient discoveryClient;
	private volatile Map<String, Service[]> serviceMap;
	private final ConcurrentHashMap<String, State> stateMap = new ConcurrentHashMap<>();

	public DefaultDiscoveryLoadBalancer(DiscoveryClient discoveryClient) {
		// 默认使用轮询
		this(Selector.roundRobin(), discoveryClient);
	}

	public DefaultDiscoveryLoadBalancer(Selector<Service> selector, DiscoveryClient discoveryClient) {
		super(selector);
		Assert.requiredArgument(discoveryClient != null, "discoveryClient");
		this.discoveryClient = discoveryClient;
	}

	private void init() {
		if (CollectionUtils.isEmpty(serviceMap)) {
			synchronized (this) {
				if (CollectionUtils.isEmpty(serviceMap)) {
					reload();
				}
			}
		}
	}

	@Override
	public Iterator<Service> iterator() {
		init();
		if (CollectionUtils.isEmpty(serviceMap)) {
			return Collections.emptyIterator();
		}

		return serviceMap.values().stream().flatMap((e) -> Arrays.asList(e).stream())
				.filter((e) -> getState(e) != State.FAILED).iterator();
	}

	public State getState(Service service) {
		return service == null ? null : stateMap.get(service.getId());
	}

	@Override
	public void reload() {
		stateMap.clear();
		List<String> names = discoveryClient.getServices();
		Map<String, Service[]> serviceMap = new HashMap<>();
		if (!CollectionUtils.isEmpty(serviceMap)) {
			for (String name : names) {
				List<Service> instances = discoveryClient.getInstances(name);
				if (CollectionUtils.isEmpty(instances)) {
					continue;
				}
				serviceMap.put(name, instances.toArray(new Service[0]));
			}
		}
		this.serviceMap = serviceMap.isEmpty() ? Collections.emptyMap() : serviceMap;
	}

	@Override
	public Elements<Service> chooses(String name) {
		// 初始化
		init();
		if (CollectionUtils.isEmpty(serviceMap)) {
			return Elements.empty();
		}

		Service[] instances = serviceMap.get(name);
		if (instances == null) {
			return Elements.empty();
		}

		return Elements.of(Arrays.asList(instances)).filter((e) -> getState(e) != State.FAILED);
	}

	@Override
	public void stat(Service service, State state) {
		Assert.requiredArgument(service != null, "service");
		Assert.requiredArgument(state != null, "state");
		stateMap.put(service.getId(), state);
	}
}
