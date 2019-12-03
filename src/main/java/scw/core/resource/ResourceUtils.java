package scw.core.resource;

import java.io.InputStream;

import scw.core.Converter;
import scw.core.utils.StringUtils;
import scw.core.utils.SystemPropertyUtils;
import scw.core.utils.SystemUtils;

/**
 * 资源工具
 * 
 * @author scw
 */
public final class ResourceUtils {
	private ResourceUtils() {
	};

	private static final MultiResourceLookup RESOURCE_LOOKUP = new MultiResourceLookup();
	private static final ResourceOperations RESOURCE_OPERATIONS;

	static {
		RESOURCE_LOOKUP.add(new DefaultResourceLookup(SystemPropertyUtils.getResourcePrefix(false),
				SystemUtils.isJar(), SystemPropertyUtils.getWorkPath(), false));
		RESOURCE_OPERATIONS = new SystemPropertyMultiSuffixResourceOperations(RESOURCE_LOOKUP);
	}

	public static final ResourceOperations getResourceOperations() {
		return RESOURCE_OPERATIONS;
	}

	public static final MultiResourceLookup getResourceLookup() {
		return RESOURCE_LOOKUP;
	}

	public static <T> T getResource(String resource, Converter<InputStream, T> converter,
			ResourceLookup resourceLookup) {
		if (StringUtils.isEmpty(resource)) {
			return null;
		}

		InputStreamConvertConsumer<T> inputStreamConvertConsumer = new InputStreamConvertConsumer<T>(converter);
		resourceLookup.lookup(resource, inputStreamConvertConsumer);
		return inputStreamConvertConsumer.getValue();
	}
}
