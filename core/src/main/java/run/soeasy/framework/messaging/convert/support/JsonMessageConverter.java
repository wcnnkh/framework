package run.soeasy.framework.messaging.convert.support;

import java.util.Arrays;

import run.soeasy.framework.messaging.MediaType;

public class JsonMessageConverter extends TextMessageConverter {
	public static final MediaType JSON_ALL = new MediaType("application", "*+json");

	public JsonMessageConverter() {
		getMediaTypeRegistry().clear();
		getMediaTypeRegistry().addAll(Arrays.asList(MediaType.APPLICATION_JSON, JSON_ALL));
	}
}
