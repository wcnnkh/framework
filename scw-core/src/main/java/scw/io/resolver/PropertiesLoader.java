package scw.io.resolver;

import java.nio.charset.Charset;
import java.util.Properties;

import scw.event.Observable;
import scw.lang.Nullable;

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
