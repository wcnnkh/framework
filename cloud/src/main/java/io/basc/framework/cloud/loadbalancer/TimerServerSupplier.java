package io.basc.framework.cloud.loadbalancer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.util.Assert;
import io.basc.framework.util.CollectionUtils;

public class TimerServerSupplier<T> extends TimerTask implements ServerSupplier<T> {
	private static final Timer TIMER = new Timer(TimerServerSupplier.class.getName(), true);
	private static Logger logger = LoggerFactory.getLogger(TimerServerSupplier.class);
	private ServerSupplier<T> serverSupplier;
	private volatile List<Server<T>> servers;
	private long period;
	private ConcurrentHashMap<String, Long> failMap = new ConcurrentHashMap<String, Long>(8);
	private AtomicBoolean timerTag = new AtomicBoolean(false);

	public TimerServerSupplier(ServerSupplier<T> serverSupplier) {
		this(serverSupplier, 60000L);
	}

	public TimerServerSupplier(ServerSupplier<T> serverSupplier, long period) {
		Assert.requiredArgument(serverSupplier != null, "serverSupplier");
		this.serverSupplier = serverSupplier;
		this.period = period;
	}

	public ServerSupplier<T> getServerSupplier() {
		return serverSupplier;
	}

	public List<Server<T>> getBasicServers(List<Server<T>> servers) {
		if (CollectionUtils.isEmpty(servers)) {
			return Collections.emptyList();
		}

		List<Server<T>> list = new ArrayList<Server<T>>(servers.size());
		for (Server<T> server : servers) {
			if (server == null) {
				continue;
			}

			if (failMap.containsKey(server.getId())) {
				continue;
			}

			int weight = server.getWeight();
			if (weight < 0) {
				continue;
			}
			list.add(server);
		}
		return list;
	}

	public boolean start() {
		if (!timerTag.get() && timerTag.compareAndSet(false, true)) {
			TIMER.schedule(this, period, period);
			return true;
		}
		return false;
	}

	public List<Server<T>> getServers() {
		if (servers == null) {
			synchronized (this) {
				// 加锁，防止大量调用同时获取原始列表
				if (servers == null) {
					List<Server<T>> servers = serverSupplier.getServers();
					if (CollectionUtils.isEmpty(servers)) {
						// 如果没有数据直接返回， 不缓存，这样下次调用还是从原始数据中获取
						return Collections.emptyList();
					}

					this.servers = getBasicServers(servers);
					start();
				}
			}
		}
		return Collections.unmodifiableList(servers);
	}

	@Override
	public void run() {
		this.servers = getServers();
		if (logger.isDebugEnabled()) {
			logger.debug("Regularly refresh the service list: {}", servers);
		}
		for (String id : failMap.keySet()) {
			if ((System.currentTimeMillis() - failMap.get(id)) > (period * 3)) {
				failMap.remove(id);
			}
		}
	}

	public void stat(Server<T> server, State state) {
		if (state == State.FAILED) {
			failMap.put(server.getId(), System.currentTimeMillis());
		}
	}
}
