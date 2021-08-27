package io.basc.framework.aop.support;

import io.basc.framework.aop.ConfigurableProxyFactory;
import io.basc.framework.aop.ProxyFactory;
import io.basc.framework.core.OrderComparator;
import io.basc.framework.core.utils.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class DefaultProxyFactory extends AbstractConfigurableProxyFactory
		implements ConfigurableProxyFactory {
	private volatile List<ProxyFactory> proxyFactories;

	public Iterator<ProxyFactory> iterator() {
		if (proxyFactories == null) {
			return Collections.emptyIterator();
		}
		return CollectionUtils.getIterator(proxyFactories, true);
	}

	public void addProxyFactory(ProxyFactory proxyFactory) {
		if (proxyFactory == null) {
			return;
		}
		synchronized (this) {
			if (proxyFactories == null) {
				proxyFactories = new ArrayList<ProxyFactory>();
			}

			this.proxyFactories.add(proxyFactory);
			Collections.sort(proxyFactories,
					OrderComparator.INSTANCE.reversed());
		}
	}
}
