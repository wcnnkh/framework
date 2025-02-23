package io.basc.framework.net.convert.support;

import java.util.Arrays;

import io.basc.framework.json.JsonSupport;
import io.basc.framework.json.JsonSupportAware;
import io.basc.framework.json.JsonUtils;
import io.basc.framework.net.MediaType;

public class JsonMessageConverter extends TextMessageConverter implements JsonSupportAware {
	public static final MediaType JSON_ALL = new MediaType("application", "*+json");

	public JsonMessageConverter() {
		getMediaTypeRegistry().clear();
		getMediaTypeRegistry().addAll(Arrays.asList(MediaType.APPLICATION_JSON, JSON_ALL));
		setConversionService(JsonUtils.getSupport());
	}

	@Override
	public void setJsonSupport(JsonSupport jsonSupport) {
		setConversionService(jsonSupport);
	}
}
