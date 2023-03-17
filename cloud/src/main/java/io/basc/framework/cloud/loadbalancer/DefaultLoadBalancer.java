package io.basc.framework.cloud.loadbalancer;

import java.util.List;
import java.util.function.Predicate;

import io.basc.framework.util.Assert;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.Selector;

public class DefaultLoadBalancer<T> extends AbstractLoadBalancer<T> {
	private final Selector<Server<T>> selector;

	public DefaultLoadBalancer(ServerSupplier<T> serverSupplier, Selector<Server<T>> selector) {
		super(serverSupplier);
		Assert.requiredArgument(selector != null, "selector");
		this.selector = selector;
	}

	public Server<T> choose(Predicate<Server<T>> accept) {
		return choose(getServerSupplier().getServers(), accept);
	}

	public Server<T> choose(List<Server<T>> servers, Predicate<Server<T>> accept) {
		if (CollectionUtils.isEmpty(servers)) {
			return null;
		}

		return accept == null ? selector.select(servers) : selector.select(servers.stream().filter(accept));
	}
}
