package io.basc.framework.context.primary.component;

public class DefaultApplicationContextComponentLoader extends ConfigurableApplicationContextComponentLoader {

	public DefaultApplicationContextComponentLoader() {
		doNativeConfigure();
		getExtender().doNativeConfigure();
	}
}
