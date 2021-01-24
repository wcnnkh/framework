package scw.configure.convert;

import scw.configure.Configure;
import scw.configure.resolver.ResourceResolver;
import scw.convert.support.DefaultConversionServiceFactory;
import scw.instance.factory.NoArgsInstanceFactory;

public class ConfigureConversionServiceFactory extends
		DefaultConversionServiceFactory {
	
	public ConfigureConversionServiceFactory(Configure configure, ResourceResolver resourceResolver, NoArgsInstanceFactory instanceFactory){
		super();
		services.add(new DocumentParseConversionService());
		services.add(new NodeListToCollectionConversionService(this));
		services.add(new NodeListToMapConversionService(this));
		services.add(new NodeToObjectConversionService(this));
		services.add(new NodeListToEntityConversionService(this, instanceFactory));
		services.add(new ConfigureConversionService(configure, instanceFactory));
		services.add(new ResourceConversionService(resourceResolver));
		services.add(new CollectionToMapConversionService(this, PrimaryKeyGetter.ANNOTATION));
		services.add(new ArrayToMapConversionService(this, PrimaryKeyGetter.ANNOTATION));
	}
}
