package scw.io.resource;

import java.io.InputStream;

import scw.core.Consumer;
import scw.core.utils.ClassUtils;
import scw.core.utils.StringUtils;
import scw.io.IOUtils;

public class ClassLoaderResourceLookup implements ResourceLookup {
	private static final String DEFAULT_PREFIX = "resources/";

	public boolean lookup(String resource, Consumer<InputStream> consumer) {
		if (StringUtils.isEmpty(resource)) {
			return false;
		}

		String rs = (resource.startsWith("/") ? resource.substring(1) : resource);
		InputStream inputStream = getInputStream(rs);
		if (inputStream == null) {
			inputStream = getInputStream(DEFAULT_PREFIX + rs);
		}

		if (inputStream == null) {
			return false;
		}

		if (consumer != null) {
			try {
				consumer.consume(inputStream);
			} catch (Throwable e) {
				throw new RuntimeException(e);
			} finally {
				IOUtils.close(inputStream);
			}
		}
		return true;
	}

	private static InputStream getInputStream(String resource) {
		InputStream inputStream = ClassUtils.getDefaultClassLoader().getResourceAsStream(resource);
		if (inputStream == null) {
			inputStream = ClassLoader.getSystemClassLoader().getResourceAsStream(resource);
		}
		return inputStream;
	}
}
