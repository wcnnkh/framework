package scw.aop.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import scw.aop.ConfigurableProxyFactory;
import scw.aop.ProxyFactory;
import scw.core.OrderComparator;
import scw.core.utils.CollectionUtils;
import scw.instance.ServiceLoaderFactory;

public class DefaultProxyFactory extends AbstractConfigurableProxyFactory implements ConfigurableProxyFactory{
	private volatile List<ProxyFactory> proxyFactories;

	public Iterator<ProxyFactory> iterator() {
		if(proxyFactories == null){
			return Collections.emptyIterator();
		}
		return CollectionUtils.getIterator(proxyFactories, true);
	}
	
	public void addProxyFactory(ProxyFactory proxyFactory) {
		if(proxyFactory == null){
			return ;
		}
		synchronized (this) {
			if(proxyFactories == null){
				proxyFactories = new ArrayList<ProxyFactory>();
				this.proxyFactories.add(proxyFactory);
				Collections.sort(proxyFactories, OrderComparator.INSTANCE.reversed());
			}
		}
	}
	
	private final AtomicBoolean loaded = new AtomicBoolean();
	public boolean loadServices(ServiceLoaderFactory serviceLoaderFactory){
		if(loaded.compareAndSet(false, true)){
			for(ProxyFactory proxyFactory : serviceLoaderFactory.getServiceLoader(ProxyFactory.class)){
				addProxyFactory(proxyFactory);
			}
			return true;
		}
		return false;
	}
}
