package io.basc.framework.net.convert.support;

import java.io.IOException;
import java.nio.charset.Charset;

import io.basc.framework.core.convert.Data;
import io.basc.framework.core.convert.TargetDescriptor;
import io.basc.framework.net.MediaType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class QueryStringMessageConveter extends TextMessageConverter {
	public QueryStringMessageConveter() {
		getMediaTypeRegistry().clear();
		getMediaTypeRegistry().add(MediaType.APPLICATION_FORM_URLENCODED);
	}

	@Override
	protected Object parseObject(String body, TargetDescriptor targetDescriptor) {
		QueryStringFormat queryStringFormat = new QueryStringFormat();
		queryStringFormat.setConversionService(getConversionService());
		return queryStringFormat.parseObject(body, targetDescriptor.getRequiredTypeDescriptor());
	}

	@Override
	protected String toString(Data<Object> body, MediaType contentType, Charset charset) throws IOException {
		QueryStringFormat queryStringFormat = new QueryStringFormat();
		queryStringFormat.setConversionService(getConversionService());
		queryStringFormat.setCharset(contentType.getCharset());
		return queryStringFormat.formatObject(body.get(), body.getTypeDescriptor());
	}
}
