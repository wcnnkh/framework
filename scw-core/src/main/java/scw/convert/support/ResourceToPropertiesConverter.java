package scw.convert.support;

import java.nio.charset.Charset;
import java.util.Properties;

import scw.convert.Converter;
import scw.io.Resource;
import scw.io.resolver.PropertiesResolver;
import scw.lang.Nullable;
import scw.util.StaticSupplier;
import scw.util.Supplier;

public class ResourceToPropertiesConverter implements Converter<Resource, Properties>{
	private final PropertiesResolver propertiesResolver;
	private final Supplier<Charset> charset;
	
	public ResourceToPropertiesConverter(PropertiesResolver propertiesResolver, @Nullable Charset charset){
		this(propertiesResolver, new StaticSupplier<Charset>(charset));
	}
	
	public ResourceToPropertiesConverter(PropertiesResolver propertiesResolver, @Nullable Supplier<Charset> charset){
		this.propertiesResolver = propertiesResolver;
		this.charset = charset;
	}
	
	public Properties convert(Resource resource) {
		Properties properties = new Properties();
		if(resource.exists()){
			return properties;
		}
		propertiesResolver.resolveProperties(properties, resource, charset == null? null:charset.get());
		return properties;
	}

}
