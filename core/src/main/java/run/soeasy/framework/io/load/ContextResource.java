package run.soeasy.framework.io.load;

import run.soeasy.framework.io.Resource;

public interface ContextResource extends Resource {

	/**
	 * Return the path within the enclosing 'context'.
	 * <p>
	 * This is typically path relative to a context-specific root directory, e.g. a
	 * ServletContext root or a PortletContext root.
	 */
	String getPathWithinContext();

}
