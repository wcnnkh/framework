package io.basc.framework.net.client.convert;

public class ConfigurableClientMessageConverter extends ClientMessageConverters<ClientMessageConverter>
		implements ClientMessageConverter {
	private static volatile ConfigurableClientMessageConverter global;

	public static ConfigurableClientMessageConverter global() {
		if (global == null) {
			synchronized (ConfigurableClientMessageConverter.class) {
				if (global == null) {
					global = new ConfigurableClientMessageConverter();
					global.doNativeConfigure();
				}
			}
		}
		return global;
	}

	public ConfigurableClientMessageConverter() {
		setServiceClass(ClientMessageConverter.class);
	}
}
