package io.basc.framework.cloud.loadbalancer;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import io.basc.framework.logger.Levels;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.util.Assert;
import io.basc.framework.util.LRULinkedHashMap;
import io.basc.framework.util.Selector;
import io.basc.framework.util.ServiceRegistry;

public class DefaultLoadBalancer<T extends Node> extends AbstractLoadBalancer<T> {
	private static Logger logger = LoggerFactory.getLogger(DefaultLoadBalancer.class);
	private final ServiceRegistry<T> registry;
	private volatile LRULinkedHashMap<String, State> stateMap = new LRULinkedHashMap<>(256);

	public DefaultLoadBalancer(Selector<T> selector, ServiceRegistry<T> registry) {
		super(selector);
		Assert.requiredArgument(registry != null, "registry");
		this.registry = registry;
	}

	public ServiceRegistry<T> getRegistry() {
		return registry;
	}

	public State getState(T service) {
		if (service == null) {
			return null;
		}
		return stateMap.get(service.getId());
	}

	@Override
	public Iterator<T> iterator() {
		ReadLock readLock = getRegistry().getLock().readLock();
		try {
			readLock.lock();
			List<T> list = getRegistry().getElements().filter((server) -> getState(server) != State.FAILED).toList();
			return list.iterator();
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
			long size = count();
			if (size < 512) {
				stateMap.clear();
			} else {
				stateMap = new LRULinkedHashMap<>(Math.max(Short.MAX_VALUE, (int) size));
			}
		} finally {
			writeLock.unlock();
		}
	}
}
