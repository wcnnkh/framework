package run.soeasy.framework.util.io.load;

import run.soeasy.framework.lang.ClassLoaderProvider;
import run.soeasy.framework.util.io.Resource;

public interface ResourceLoader extends ClassLoaderProvider {
	/**
	 * Return a Resource handle for the specified resource location.
	 */
	Resource getResource(String location);
}
