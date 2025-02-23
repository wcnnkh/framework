package io.basc.framework.net.convert.support;

public class DefaultMessageConverters extends ConfigurableMessageConverter {

	public DefaultMessageConverters() {
		setLast(GlobalMessageConverter.getInstance());
	}
}
