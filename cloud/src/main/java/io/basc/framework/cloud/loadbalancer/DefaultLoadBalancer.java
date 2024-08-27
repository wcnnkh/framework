package io.basc.framework.cloud.loadbalancer;

import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import io.basc.framework.observe.service.ObservableServiceLoader;
import io.basc.framework.util.Assert;
import io.basc.framework.util.Elements;
import io.basc.framework.util.collect.LRULinkedHashMap;
import io.basc.framework.util.logging.Levels;
import io.basc.framework.util.logging.Logger;
import io.basc.framework.util.logging.LoggerFactory;
import io.basc.framework.util.select.Selector;

public class DefaultLoadBalancer<T extends Node> extends AbstractLoadBalancer<T> {
	private static Logger logger = LoggerFactory.getLogger(DefaultLoadBalancer.class);
	private final ObservableServiceLoader<T> registry;
	private volatile LRULinkedHashMap<String, State> stateMap = new LRULinkedHashMap<>(256);

	public DefaultLoadBalancer(Selector<T> selector, ObservableServiceLoader<T> registry) {
		super(selector);
		Assert.requiredArgument(registry != null, "registry");
		this.registry = registry;
	}

	public ObservableServiceLoader<T> getRegistry() {
		return registry;
	}

	public State getState(T service) {
		if (service == null) {
			return null;
		}
		return stateMap.get(service.getId());
	}

	@Override
	public Elements<T> getServices() {
		ReadLock readLock = getRegistry().getLock().readLock();
		try {
			readLock.lock();
			return getRegistry().getServices().filter((server) -> getState(server) != State.FAILED);
		} finally {
			readLock.unlock();
		}
	}

	public void stat(T service, State state) {
		Assert.requiredArgument(service != null, "service");
		Assert.requiredArgument(state != null, "state");
		logger.log(state == State.FAILED ? Levels.INFO.getValue() : Levels.DEBUG.getValue(),
				"Stat service [{}] state [{}]", service, state);
		WriteLock writeLock = getRegistry().getLock().writeLock();
		try {
			writeLock.lock();
			stateMap.put(service.getId(), state);
		} finally {
			writeLock.unlock();
		}
	}

	@Override
	public void reload() {
		WriteLock writeLock = getRegistry().getLock().writeLock();
		try {
			writeLock.lock();
			// 重新构造
			getRegistry().reload();
			stateMap.clear();
		} finally {
			writeLock.unlock();
		}
	}
}
