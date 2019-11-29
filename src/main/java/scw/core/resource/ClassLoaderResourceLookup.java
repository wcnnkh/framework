package scw.core.resource;

import java.io.InputStream;

import scw.core.Consumer;
import scw.core.utils.ClassUtils;
import scw.core.utils.StringUtils;
import scw.io.IOUtils;

public class ClassLoaderResourceLookup implements ResourceLookup {

	public boolean lookup(String resource, Consumer<InputStream> consumer) {
		if (StringUtils.isEmpty(resource)) {
			return false;
		}

		InputStream inputStream = ClassLoaderResourceLookup.class.getResourceAsStream(resource);
		if (inputStream == null) {
			try {
				inputStream = ClassUtils.getDefaultClassLoader().getResourceAsStream(resource);
			} catch (Exception e) {
				// ignore 在一些特殊情况下可能出现异常，忽略此异常
			}
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

}
