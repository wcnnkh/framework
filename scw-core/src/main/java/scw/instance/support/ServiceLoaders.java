package scw.instance.support;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import scw.core.utils.CollectionUtils;
import scw.instance.ServiceLoader;
import scw.util.MultiIterable;

public class ServiceLoaders<S> implements ServiceLoader<S>{
	private List<ServiceLoader<S>> serviceLoaders;
	
	public ServiceLoaders(ServiceLoader<S> ...serviceLoaders){
		this(Arrays.asList(serviceLoaders));
	}
	
	public ServiceLoaders(List<ServiceLoader<S>> serviceLoaders){
		this.serviceLoaders = CollectionUtils.isEmpty(serviceLoaders)? null:serviceLoaders;
	}
	
	public void reload() {
		if(serviceLoaders == null){
			return ;
		}
		
		for(ServiceLoader<S> serviceLoader : serviceLoaders){
			if(serviceLoader == null){
				continue;
			}
			
			serviceLoader.reload();
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Iterator<S> iterator() {
		return new MultiIterable(serviceLoaders).iterator();
	}
}
