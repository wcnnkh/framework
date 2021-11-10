package io.basc.framework.cloud.loadbalancer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import io.basc.framework.util.CollectionUtils;

public class RoundRobinLoadBalancer<T> extends AbstractLoadBalancer<T> {
	private AtomicInteger position = new AtomicInteger();

	public RoundRobinLoadBalancer(ServerSupplier<T> serverSupplier) {
		super(serverSupplier);
	}

	public Server<T> choose(ServerAccept<T> accept) {
		return choose(this.position, getServerSupplier().getServers(), accept);
	}

	public Server<T> choose(AtomicInteger position, List<Server<T>> servers,
			ServerAccept<T> accept) {
		if (CollectionUtils.isEmpty(servers)) {
			return null;
		}

		int pos = Math.abs(position.getAndIncrement());
		int total = 0;
		List<Server<T>> list = new ArrayList<Server<T>>(servers.size());
		for (Server<T> server : servers) {
			if (server == null) {
				continue;
			}

			int weight = server.getWeight();
			if (weight < 0) {
				continue;
			}

			if (accept != null && !accept.accept(server)) {
				continue;
			}

			total += weight == 0 ? 1 : server.getWeight();
			list.add(server);
		}
		
		if(total == 0) {
			return null;
		}

		pos = pos % total;
		return list.get(pos);
	}
}
