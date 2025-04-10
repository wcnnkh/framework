package run.soeasy.framework.util.io.load;

import java.net.MalformedURLException;
import java.net.URL;

import lombok.Getter;
import run.soeasy.framework.lang.DefaultClassLoaderAccessor;
import run.soeasy.framework.util.Assert;
import run.soeasy.framework.util.StringUtils;
import run.soeasy.framework.util.exchange.Receipt;
import run.soeasy.framework.util.io.ClassPathResource;
import run.soeasy.framework.util.io.Resource;
import run.soeasy.framework.util.io.UrlResource;
import run.soeasy.framework.util.spi.Configurable;
import run.soeasy.framework.util.spi.ProviderFactory;

@Getter
public class DefaultResourceLoader extends DefaultClassLoaderAccessor implements ResourceLoader, Configurable {
	private final ProtocolResolvers protocolResolvers = new ProtocolResolvers();

	@Override
	public Receipt configure(ProviderFactory discovery) {
		return protocolResolvers.configure(discovery);
	}

	public Resource getResource(String location) {
		Assert.notNull(location, "Location must not be null");

		Resource resource = protocolResolvers.resolve(location, this);
		if (resource != null) {
			return resource;
		}

		if (location.startsWith("/")) {
			return getResourceByPath(location);
		} else {
			try {
				// Try to parse the location as a URL...
				URL url = new URL(location);
				return new UrlResource(url);
			} catch (MalformedURLException ex) {
				// No URL -> resolve as resource path.
				return getResourceByPath(location);
			}
		}
	}

	protected Resource getResourceByPath(String path) {
		return new ClassPathContextResource(path, getClassLoader());
	}

	protected static class ClassPathContextResource extends ClassPathResource implements ContextResource {

		public ClassPathContextResource(String path, ClassLoader classLoader) {
			super(path, classLoader);
		}

		public String getPathWithinContext() {
			return getPath();
		}

		@Override
		public Resource createRelative(String relativePath) {
			String pathToUse = StringUtils.applyRelativePath(getPath(), relativePath);
			return new ClassPathContextResource(pathToUse, getClassLoader());
		}
	}
}
