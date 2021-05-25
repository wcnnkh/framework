package scw.aop.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import scw.aop.ConfigurableProxyFactory;
import scw.aop.ProxyFactory;
import scw.core.OrderComparator;
import scw.core.utils.CollectionUtils;

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
