package run.soeasy.framework.core.io.load;

import run.soeasy.framework.core.io.Resource;
import run.soeasy.framework.core.lang.ClassLoaderProvider;

public interface ResourceLoader extends ClassLoaderProvider {
	/**
	 * Return a Resource handle for the specified resource location.
	 */
	Resource getResource(String location);
}
