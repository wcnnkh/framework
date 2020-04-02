package scw.net.message.converter.support;

import scw.json.JSONUtils;
import scw.net.message.converter.ByteArrayMessageConverter;
import scw.net.message.converter.JsonMessageConverter;
import scw.net.message.converter.MultiMessageConverter;
import scw.net.message.converter.StringMessageConverter;

public class AllMessageConverter extends MultiMessageConverter{
	private static final long serialVersionUID = 1L;
	
	public AllMessageConverter() {
		add(new JsonMessageConverter(JSONUtils.DEFAULT_JSON_SUPPORT));
		add(new StringMessageConverter());
		add(new ByteArrayMessageConverter());
	}
}
