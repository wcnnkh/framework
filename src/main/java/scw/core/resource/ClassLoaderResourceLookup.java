package scw.core.resource;

import java.io.InputStream;

import scw.core.Consumer;
import scw.core.utils.ClassUtils;
import scw.core.utils.StringUtils;
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
		if(StringUtils.isEmpty(name)){
			return null;
		}
		
		InputStream inputStream = ClassLoaderResourceLookup.class.getResourceAsStream(name);
		if (inputStream == null) {
			try {
				inputStream = ClassUtils.getDefaultClassLoader().getResourceAsStream(name);
			} catch (Exception e) {
				//ignore 在一些特殊情况下可能出现异常，忽略此异常
			}
		}
		return inputStream;
	}
}
