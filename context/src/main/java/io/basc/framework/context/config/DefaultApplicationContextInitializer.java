package io.basc.framework.context.config;

public class DefaultApplicationContextInitializer extends ConfigurableApplicationContextInitializer {

	public DefaultApplicationContextInitializer() {
		doNativeConfigure();
	}
}
