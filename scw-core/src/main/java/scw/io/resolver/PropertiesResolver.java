package scw.io.resolver;

import java.nio.charset.Charset;
import java.util.Properties;

import scw.io.Resource;
import scw.lang.Nullable;

public interface PropertiesResolver {
	boolean canResolveProperties(Resource resource);
	
	void resolveProperties(Properties properties, Resource resource, @Nullable Charset charset);
}
