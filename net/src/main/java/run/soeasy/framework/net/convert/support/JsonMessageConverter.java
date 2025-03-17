package run.soeasy.framework.net.convert.support;

import java.util.Arrays;

import run.soeasy.framework.net.MediaType;

public class JsonMessageConverter extends TextMessageConverter {
	public static final MediaType JSON_ALL = new MediaType("application", "*+json");

	public JsonMessageConverter() {
		getMediaTypeRegistry().clear();
		getMediaTypeRegistry().addAll(Arrays.asList(MediaType.APPLICATION_JSON, JSON_ALL));
	}
}
