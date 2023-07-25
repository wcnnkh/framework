package io.basc.framework.cloud.loadbalancer;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.basc.framework.cloud.DiscoveryClient;
import io.basc.framework.cloud.Service;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.util.Assert;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.element.Elements;
import io.basc.framework.util.select.Selector;

public class DefaultDiscoveryLoadBalancer extends AbstractLoadBalancer<Service> implements DiscoveryLoadBalancer {
	private static Logger logger = LoggerFactory.getLogger(DefaultDiscoveryLoadBalancer.class);
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

	private void touch() {
		if (CollectionUtils.isEmpty(serviceMap)) {
			synchronized (this) {
				if (CollectionUtils.isEmpty(serviceMap)) {
					reload();
				}
			}
		}
	}

	@Override
	public Elements<Service> getServices() {
		touch();
		if (CollectionUtils.isEmpty(serviceMap)) {
			return Elements.empty();
		}

		return Elements.of(() -> serviceMap.values().stream().flatMap((e) -> Arrays.asList(e).stream())
				.filter((e) -> getState(e) != State.FAILED));
	}

	public State getState(Service service) {
		return service == null ? null : stateMap.get(service.getId());
	}

	@Override
	public void reload() {
		stateMap.clear();
		List<String> names = discoveryClient.getServices();
		Map<String, Service[]> serviceMap = new HashMap<>();
		for (String name : names) {
			List<Service> instances = discoveryClient.getInstances(name);
			if (CollectionUtils.isEmpty(instances)) {
				continue;
			}

			logger.info("reload application[{}] services {}", name, instances);
			serviceMap.put(name, instances.toArray(new Service[0]));
		}
		this.serviceMap = serviceMap.isEmpty() ? Collections.emptyMap() : serviceMap;
	}

	@Override
	public Elements<Service> chooses(String name) {
		// 初始化
		touch();
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

	@Override
	public String toString() {
		return serviceMap == null ? "{}" : serviceMap.toString();
	}
}
