package io.basc.framework.net.convert;

public class DefaultMessageConverters extends ConfigurableMessageConverter {

	public DefaultMessageConverters() {
		setLastService(GlobalMessageConverter.getInstance());
	}
}
