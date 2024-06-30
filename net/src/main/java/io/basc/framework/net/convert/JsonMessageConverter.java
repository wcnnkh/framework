package io.basc.framework.net.convert;

import io.basc.framework.json.JsonSupport;
import io.basc.framework.json.JsonSupportAware;
import io.basc.framework.json.JsonUtils;
import io.basc.framework.net.MimeType;
import io.basc.framework.net.MimeTypeUtils;

public class JsonMessageConverter extends TextMessageConverter implements JsonSupportAware {
	public static final MimeType JSON_ALL = new MimeType("application", "*+json");

	public JsonMessageConverter() {
		getMimeTypes().getMimeTypes().clear();
		getMimeTypes().add(MimeTypeUtils.APPLICATION_JSON, JSON_ALL);
		setConversionService(JsonUtils.getSupport());
	}

	@Override
	public void setJsonSupport(JsonSupport jsonSupport) {
		setConversionService(jsonSupport);
	}
}
