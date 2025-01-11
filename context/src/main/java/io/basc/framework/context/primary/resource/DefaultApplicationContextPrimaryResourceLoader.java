package io.basc.framework.context.primary.resource;

public class DefaultApplicationContextPrimaryResourceLoader
		extends ConfigurableApplicationContextPrimaryResourceLoader {

	public DefaultApplicationContextPrimaryResourceLoader() {
		doNativeConfigure();
		getExtender().doNativeConfigure();
	}
}
