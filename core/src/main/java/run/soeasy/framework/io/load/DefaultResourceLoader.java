package run.soeasy.framework.io.load;

import java.net.MalformedURLException;
import java.net.URL;

import lombok.Getter;
import lombok.NonNull;
import run.soeasy.framework.core.DefaultClassLoaderAccessor;
import run.soeasy.framework.core.StringUtils;
import run.soeasy.framework.core.exchange.Receipt;
import run.soeasy.framework.core.spi.Configurable;
import run.soeasy.framework.core.spi.ProviderFactory;
import run.soeasy.framework.io.Resource;

@Getter
public class DefaultResourceLoader extends DefaultClassLoaderAccessor implements ResourceLoader, Configurable {
	private final ProtocolResolvers protocolResolvers = new ProtocolResolvers();

	@Override
	public Receipt configure(ProviderFactory discovery) {
		return protocolResolvers.configure(discovery);
	}

	public Resource getResource(@NonNull String location) {
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
