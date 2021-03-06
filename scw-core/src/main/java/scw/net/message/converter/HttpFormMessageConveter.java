package scw.net.message.converter;

import java.io.IOException;
import java.util.Collection;

import scw.codec.support.URLCodec;
import scw.core.ResolvableType;
import scw.core.utils.StringUtils;
import scw.http.MediaType;
import scw.net.MimeType;
import scw.net.message.InputMessage;
import scw.net.message.OutputMessage;
import scw.net.uri.UriUtils;
import scw.util.MultiValueMap;

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
	protected Object readInternal(ResolvableType type, InputMessage inputMessage) throws IOException, MessageConvertException {
		String content = readTextBody(inputMessage);
		if (StringUtils.isEmpty(content)) {
			return null;
		}

		MultiValueMap<String, String> map = UriUtils.getQueryParams(content);
		String json = getJsonSupport().toJSONString(map);
		return getJsonSupport().parseObject(json, type.getType());
	}

	@Override
	protected void writeInternal(ResolvableType type, Object body, MimeType contentType, OutputMessage outputMessage)
			throws IOException, MessageConvertException {
		String queryString = UriUtils.toQueryString(body, new URLCodec(getCharset(outputMessage)));
		writeTextBody(queryString, contentType, outputMessage);
	}

}
