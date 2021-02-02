package scw.aop.support;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import scw.aop.ConfigurableProxyFactory;
import scw.aop.ProxyFactory;
import scw.core.utils.CollectionUtils;
import scw.instance.ServiceLoaderFactory;

public class DefaultConfigurableProxyFactory extends AbstractConfigurableProxyFactory implements ConfigurableProxyFactory{
	private volatile List<ProxyFactory> proxyFactories;

	public Iterator<ProxyFactory> iterator() {
		if(proxyFactories == null){
			return Collections.emptyIterator();
		}
		return CollectionUtils.getIterator(proxyFactories, true);
	}
	
	public void addProxyFactory(ProxyFactory proxyFactory) {
		if(proxyFactories == null){
			synchronized (this) {
				if(proxyFactories == null){
					proxyFactories = new CopyOnWriteArrayList<ProxyFactory>();
				}
			}
		}
		this.proxyFactories.add(proxyFactory);
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
