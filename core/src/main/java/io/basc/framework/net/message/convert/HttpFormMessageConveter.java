package io.basc.framework.net.message.convert;

import io.basc.framework.codec.support.URLCodec;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.http.MediaType;
import io.basc.framework.net.MimeType;
import io.basc.framework.net.message.InputMessage;
import io.basc.framework.net.message.OutputMessage;
import io.basc.framework.net.uri.UriUtils;
import io.basc.framework.util.MultiValueMap;
import io.basc.framework.util.StringUtils;

import java.io.IOException;
import java.util.Collection;

public class HttpFormMessageConveter extends AbstractMessageConverter<Object> {

	public HttpFormMessageConveter() {
		supportMimeTypes.add(MediaType.APPLICATION_FORM_URLENCODED);
	}

	@Override
	public boolean support(Class<?> clazz) {
		if (Collection.class.isAssignableFrom(clazz)) {
			return false;
		}
		return true;
	}

	@Override
	protected Object readInternal(TypeDescriptor type, InputMessage inputMessage)
			throws IOException, MessageConvertException {
		String content = readTextBody(inputMessage);
		if (StringUtils.isEmpty(content)) {
			return null;
		}

		MultiValueMap<String, String> map = UriUtils.getQueryParams(content);
		String json = getJsonSupport().toJsonString(map);
		return getJsonSupport().parseObject(json, type.getType());
	}

	@Override
	protected void writeInternal(TypeDescriptor type, Object body, MimeType contentType, OutputMessage outputMessage)
			throws IOException, MessageConvertException {
		String queryString = UriUtils.toQueryString(body, new URLCodec(getCharset(outputMessage)));
		writeTextBody(queryString, contentType, outputMessage);
	}

}
