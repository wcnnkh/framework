package io.basc.framework.net.convert.support;

import io.basc.framework.net.convert.MessageConverters;

public class GlobalMessageConverters extends MessageConverters {
	private static volatile GlobalMessageConverters instance;

	public static GlobalMessageConverters getInstance() {
		if (instance == null) {
			synchronized (GlobalMessageConverters.class) {
				if (instance == null) {
					instance = new GlobalMessageConverters();
					instance.doNativeConfigure();
				}
			}
		}
		return instance;
	}

	private final TextMessageConverter textMessageConverter = new TextMessageConverter();
	private final ByteArrayMessageConverter byteArrayMessageConverter = new ByteArrayMessageConverter();
	private final QueryStringMessageConveter queryStringMessageConveter = new QueryStringMessageConveter();

	private GlobalMessageConverters() {
		register(textMessageConverter);
		register(byteArrayMessageConverter);
		register(queryStringMessageConveter);
	}

}
