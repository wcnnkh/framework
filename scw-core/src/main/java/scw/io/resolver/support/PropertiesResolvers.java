package scw.io.resolver.support;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import scw.core.OrderComparator;
import scw.core.utils.CollectionUtils;
import scw.io.Resource;
import scw.io.resolver.ConfigurablePropertiesResolver;
import scw.io.resolver.PropertiesResolver;
import scw.lang.NotSupportedException;

public class PropertiesResolvers extends DefaultPropertiesResolver implements ConfigurablePropertiesResolver, Comparator<PropertiesResolver>{
	protected volatile List<PropertiesResolver> resolvers;
	
	public int compare(PropertiesResolver o1, PropertiesResolver o2) {
		return -1;
	}
	
	public void addPropertiesResolver(PropertiesResolver propertiesResolver) {
		if(propertiesResolver == null){
			return ;
		}
		
		synchronized (this) {
			if(resolvers == null){
				resolvers = new ArrayList<PropertiesResolver>(4);
			}
			resolvers.add(propertiesResolver);
			Collections.sort(resolvers, OrderComparator.INSTANCE.reversed());
		}
	}
	
	@Override
	public Iterator<PropertiesResolver> iterator() {
		if(resolvers == null){
			return Collections.emptyIterator();
		}
		
		return CollectionUtils.getIterator(resolvers, true);
	}
	
	public boolean canResolveProperties(Resource resource) {
		for(PropertiesResolver resolver : this){
			if(resolver.canResolveProperties(resource)){
				return true;
			}
		}
		return super.canResolveProperties(resource);
	}

	public void resolveProperties(Properties properties, Resource resource,
			Charset charset) {
		if (resource == null || !resource.exists()) {
			return;
		}
		
		for(PropertiesResolver resolver : this){
			if(resolver.canResolveProperties(resource)){
				resolver.resolveProperties(properties, resource, charset);
				return ;
			}
		}
		
		if(super.canResolveProperties(resource)){
			super.resolveProperties(properties, resource, charset);
			return ;
		}
		throw new NotSupportedException(resource.getDescription());
	}
}
