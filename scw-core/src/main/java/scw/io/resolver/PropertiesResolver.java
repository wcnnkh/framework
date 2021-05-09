package scw.io.resolver;

import java.nio.charset.Charset;
import java.util.Properties;

import scw.convert.Converter;
import scw.io.Resource;
import scw.lang.Nullable;

public interface PropertiesResolver {
	boolean canResolveProperties(Resource resource);
	
	void resolveProperties(Properties properties, Resource resource, @Nullable Charset charset);
	
	default Converter<Resource, Properties> toPropertiesConverter(){
		return toPropertiesConverter(null);
	}
	
	default Converter<Resource, Properties> toPropertiesConverter(Charset charset){
		return new Converter<Resource, Properties>() {
			
			@Override
			public Properties convert(Resource o) {
				Properties properties = new Properties();
				resolveProperties(properties, o, charset);
				return properties;
			}
		};
	}
}
