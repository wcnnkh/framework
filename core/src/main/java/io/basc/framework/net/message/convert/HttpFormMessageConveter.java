package io.basc.framework.net.message.convert;

import java.io.IOException;
import java.util.Collection;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.http.MediaType;
import io.basc.framework.net.MimeType;
import io.basc.framework.net.message.InputMessage;
import io.basc.framework.net.message.OutputMessage;
import io.basc.framework.net.uri.UriUtils;
import io.basc.framework.text.query.QueryStringFormat;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.collect.MultiValueMap;

public class HttpFormMessageConveter extends AbstractMessageConverter<Object> {

	public HttpFormMessageConveter() {
		supportMimeTypes.add(MediaType.APPLICATION_FORM_URLENCODED);
	}

	@Override
	public boolean isSupported(Class<?> clazz) {
		return Collection.class.isAssignableFrom(clazz);
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
		QueryStringFormat queryStringFormat = new QueryStringFormat();
		queryStringFormat.setCharset(getCharset(outputMessage));
		String queryString = queryStringFormat.formatObject(body, type);
		writeTextBody(queryString, contentType, outputMessage);
	}

}
