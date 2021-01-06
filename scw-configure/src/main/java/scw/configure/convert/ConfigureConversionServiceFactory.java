package scw.configure.convert;

import scw.configure.Configure;
import scw.configure.resolver.ResourceResolver;
import scw.convert.support.DefaultConversionServiceFactory;
import scw.core.instance.NoArgsInstanceFactory;

public class ConfigureConversionServiceFactory extends
		DefaultConversionServiceFactory {
	
	public ConfigureConversionServiceFactory(Configure configure, ResourceResolver resourceResolver, NoArgsInstanceFactory instanceFactory){
		super();
		conversionServices.add(new NodeListToCollectionConversionService(this));
		conversionServices.add(new NodeListToEntityConversionService(this, instanceFactory));
		conversionServices.add(new NodeListToMapConversionService(this));
		conversionServices.add(new NodeToObjectConversionService(this));
		conversionServices.add(new ConfigureConversionService(configure, instanceFactory));
		conversionServices.add(new ResourceConversionService(resourceResolver));
		conversionServices.add(new CollectionToMapConversionService(this, PrimaryKeyGetter.ANNOTATION));
		conversionServices.add(new ArrayToMapConversionService(this, PrimaryKeyGetter.ANNOTATION));
	}
}
