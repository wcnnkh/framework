package run.soeasy.framework.core.io.load;

import run.soeasy.framework.core.ClassLoaderProvider;
import run.soeasy.framework.core.io.Resource;

public interface ResourceLoader extends ClassLoaderProvider {
	/**
	 * Return a Resource handle for the specified resource location.
	 */
	Resource getResource(String location);
}
