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
import scw.util.XUtils;

public class PropertiesResolvers implements ConfigurablePropertiesResolver, Comparator<PropertiesResolver>{
	
	protected final TreeSet<PropertiesResolver> resolvers = new TreeSet<PropertiesResolver>(this);
	
	public PropertiesResolvers(){
		resolvers.add(DefaultPropertiesResolver.INSTANCE);
	}
	
	public SortedSet<PropertiesResolver> getResolvers(){
		return XUtils.synchronizedProxy(resolvers, this);
	}
	
	public int compare(PropertiesResolver o1, PropertiesResolver o2) {
		return -1;
	}
	
	public void addPropertiesResolver(PropertiesResolver propertiesResolver) {
		getResolvers().add(propertiesResolver);
	}
	
	public PropertiesResolver getPropertiesResolver(Resource resource){
		for(PropertiesResolver resolver : resolvers){
			if(resolver.canResolveProperties(resource)){
				return resolver;
			}
		}
		return null;
	}

	public boolean canResolveProperties(Resource resource) {
		return getPropertiesResolver(resource) != null;
	}

	public void resolveProperties(Properties properties, Resource resource,
			Charset charset) {
		PropertiesResolver resolver = getPropertiesResolver(resource);
		if(resolver == null){
			throw new NotSupportedException(resource.getDescription());
		}
		resolver.resolveProperties(properties, resource, charset);
	}
}
