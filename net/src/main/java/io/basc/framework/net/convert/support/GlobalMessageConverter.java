package io.basc.framework.net.convert.support;

import io.basc.framework.net.convert.MessageConverters;

public class GlobalMessageConverter extends MessageConverters {
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

	private GlobalMessageConverter() {
		register(textMessageConverter);
		register(byteArrayMessageConverter);
		register(queryStringMessageConveter);
	}

}
