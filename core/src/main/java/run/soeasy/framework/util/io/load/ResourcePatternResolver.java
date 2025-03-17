package run.soeasy.framework.util.io.load;

import java.io.IOException;

import run.soeasy.framework.util.io.Resource;

public interface ResourcePatternResolver extends ResourceLoader {

	/**
	 * 类路径中所有匹配资源的伪URL前缀：“class path*：”
	 */
	String CLASSPATH_ALL_URL_PREFIX = "classpath*:";

	/**
	 * Resolve the given location pattern into Resource objects.
	 * <p>
	 * Overlapping resource entries that point to the same physical resource should
	 * be avoided, as far as possible. The result should have set semantics.
	 * 
	 * @param locationPattern the location pattern to resolve
	 * @return the corresponding Resource objects
	 * @throws IOException
	 */
	Resource[] getResources(String locationPattern) throws IOException;

}
