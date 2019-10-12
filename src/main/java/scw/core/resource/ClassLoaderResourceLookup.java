package scw.core.resource;

import java.io.InputStream;

import scw.core.Consumer;
import scw.core.utils.ClassUtils;
import scw.io.IOUtils;

/**
 * 从ClassLoader中查找资源
 * 
 * @author shuchaowen
 *
 */
public class ClassLoaderResourceLookup extends ClassesResourceLookup {

	@Override
	public boolean lookup(String resource, Consumer<InputStream> consumer) {
		InputStream inputStream = getResourceAsStream(resource);
		if (inputStream == null) {
			return false;
		}

		if (consumer != null) {
			try {
				consumer.consume(inputStream);
			} catch (Exception e) {
				throw new RuntimeException(e);
			} finally {
				IOUtils.close(inputStream);
			}
		}
		return true;
	}

	public static InputStream getResourceAsStream(String name) {
		InputStream inputStream = ResourceUtils.class.getResourceAsStream(name);
		if (inputStream == null) {
			inputStream = ClassUtils.getDefaultClassLoader().getResourceAsStream(name);
		}
		return inputStream;
	}
}
