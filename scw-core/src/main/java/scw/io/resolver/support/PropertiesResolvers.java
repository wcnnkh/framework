package scw.io.resolver.support;

import java.nio.charset.Charset;
import java.util.Comparator;
import java.util.Properties;
import java.util.SortedSet;
import java.util.TreeSet;

import scw.io.Resource;
import scw.io.resolver.ConfigurablePropertiesResolver;
import scw.io.resolver.PropertiesResolver;
import scw.lang.NotSupportedException;
import scw.util.Synchronized;

public class PropertiesResolvers extends DefaultPropertiesResolver implements ConfigurablePropertiesResolver, Comparator<PropertiesResolver>{
	protected final TreeSet<PropertiesResolver> resolvers = new TreeSet<PropertiesResolver>(this);
	
	public SortedSet<PropertiesResolver> getResolvers(){
		return Synchronized.proxy(resolvers, this);
	}
	
	public int compare(PropertiesResolver o1, PropertiesResolver o2) {
		return -1;
	}
	
	public void addPropertiesResolver(PropertiesResolver propertiesResolver) {
		getResolvers().add(propertiesResolver);
	}
	
	public boolean canResolveProperties(Resource resource) {
		for(PropertiesResolver resolver : resolvers){
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
		
		for(PropertiesResolver resolver : resolvers){
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
