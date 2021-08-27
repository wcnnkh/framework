package io.basc.framework.io.resolver;

import io.basc.framework.event.Observable;
import io.basc.framework.lang.Nullable;

import java.nio.charset.Charset;
import java.util.Properties;

public interface PropertiesLoader extends PropertiesResolver{
	Observable<Properties> getProperties(String location);
	
	Observable<Properties> getProperties(String location,
			@Nullable String charsetName);
	
	Observable<Properties> getProperties(String location,
			@Nullable Charset charset);

	Observable<Properties> getProperties(PropertiesResolver propertiesResolver,
			String location);

	Observable<Properties> getProperties(PropertiesResolver propertiesResolver,
			String location, @Nullable Charset charset);

	Observable<Properties> getProperties(PropertiesResolver propertiesResolver,
			String location, @Nullable String charsetName);
}
