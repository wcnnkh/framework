package run.soeasy.framework.messaging.convert.support;

import run.soeasy.framework.messaging.convert.MessageConverters;

public final class SystemMessageConverters extends MessageConverters {
	private static volatile SystemMessageConverters instance;

	public static SystemMessageConverters getInstance() {
		if (instance == null) {
			synchronized (SystemMessageConverters.class) {
				if (instance == null) {
					instance = new SystemMessageConverters();
					instance.configure();
				}
			}
		}
		return instance;
	}

	private final TextMessageConverter textMessageConverter = new TextMessageConverter();
	private final ByteArrayMessageConverter byteArrayMessageConverter = new ByteArrayMessageConverter();
	private final QueryStringMessageConveter queryStringMessageConveter = new QueryStringMessageConveter();

	private SystemMessageConverters() {
		register(textMessageConverter);
		register(byteArrayMessageConverter);
		register(queryStringMessageConveter);
	}

}
