package io.basc.framework.net.convert;

import io.basc.framework.core.convert.config.ConversionService;
import io.basc.framework.core.convert.config.ConversionServiceAware;
import io.basc.framework.json.JsonSupport;
import io.basc.framework.json.JsonSupportAware;

public class GlobalMessageConverter extends ConfigurableMessageConverter
		implements ConversionServiceAware, JsonSupportAware {
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

	private final JsonMessageConverter jsonMessageConverter = new JsonMessageConverter();
	private final TextMessageConverter textMessageConverter = new TextMessageConverter();
	private final ByteArrayMessageConverter byteArrayMessageConverter = new ByteArrayMessageConverter();
	private final QueryStringMessageConveter queryStringMessageConveter = new QueryStringMessageConveter();

	@Override
	public void setConversionService(ConversionService conversionService) {
		textMessageConverter.setConversionService(conversionService);
	}

	@Override
	public void setJsonSupport(JsonSupport jsonSupport) {
		jsonMessageConverter.setJsonSupport(jsonSupport);
	}

	public GlobalMessageConverter() {
		register(jsonMessageConverter);
		register(textMessageConverter);
		register(byteArrayMessageConverter);
		register(queryStringMessageConveter);
	}

}
