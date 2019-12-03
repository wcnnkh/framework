package scw.core.resource;

import java.io.InputStream;

import scw.core.Consumer;
import scw.core.utils.ClassUtils;
import scw.core.utils.StringUtils;
import scw.io.IOUtils;

public class ClassLoaderResourceLookup implements ResourceLookup {
	private String prefix;
	private boolean auto;

	/**
	 * @param prefix
	 * @param auto
	 *            当查找不到时是否去除前缀查找
	 */
	public ClassLoaderResourceLookup(String prefix, boolean auto) {
		this.auto = StringUtils.isEmpty(prefix) && auto;
		String root = (prefix == null ? "" : prefix);
		root = root.replaceAll("\\\\", "/");
		this.prefix = root.endsWith("/") ? root : (root + "/");
	}

	public boolean lookup(String resource, Consumer<InputStream> consumer) {
		if (StringUtils.isEmpty(resource)) {
			return false;
		}

		InputStream inputStream = getInputStream(
				prefix + (resource.startsWith("/") ? (resource.substring(1)) : resource));
		if (auto && inputStream == null) {
			inputStream = getInputStream(resource);
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

	public static InputStream getInputStream(String resource) {
		InputStream inputStream = ClassLoaderResourceLookup.class.getResourceAsStream(resource);
		if (inputStream == null) {
			try {
				inputStream = ClassUtils.getDefaultClassLoader().getResourceAsStream(resource);
			} catch (Exception e) {
				e.printStackTrace();
				// ignore 在一些特殊情况下可能出现异常，忽略此异常
			}
		}
		return inputStream;
	}
}
