package io.basc.framework.net.convert.support;

public class GlobalMessageConverter extends ConfigurableMessageConverter {
	private static volatile GlobalMessageConverter instance;

	public static GlobalMessageConverter getInstance() {
		if (instance == null) {
			synchronized (GlobalMessageConverter.class) {
				if (instance == null) {
					instance = new GlobalMessageConverter();
					instance.doNativeConfigure();
				}
			}
		}
		return instance;
	}

	private final TextMessageConverter textMessageConverter = new TextMessageConverter();
	private final ByteArrayMessageConverter byteArrayMessageConverter = new ByteArrayMessageConverter();
	private final QueryStringMessageConveter queryStringMessageConveter = new QueryStringMessageConveter();

	public GlobalMessageConverter() {
		register(textMessageConverter);
		register(byteArrayMessageConverter);
		register(queryStringMessageConveter);
	}

}
