package io.basc.framework.convert.lang;

import io.basc.framework.convert.Converter;
import io.basc.framework.io.Resource;
import io.basc.framework.io.resolver.PropertiesResolver;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.StaticSupplier;

import java.nio.charset.Charset;
import java.util.Properties;
import java.util.function.Supplier;

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
