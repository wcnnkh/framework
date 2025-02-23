package io.basc.framework.net.convert.support;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.config.ConversionService;
import io.basc.framework.core.convert.config.ConversionServiceAware;
import io.basc.framework.net.MediaType;
import io.basc.framework.util.io.MimeType;
import lombok.Setter;

@Setter
public class QueryStringMessageConveter extends StringMessageConverter<Object> implements ConversionServiceAware {
	private ConversionService conversionService;

	public QueryStringMessageConveter() {
		getMediaTypeRegistry().add(MediaType.APPLICATION_FORM_URLENCODED);
	}

	@Override
	protected Object parseObject(String body, TypeDescriptor targetTypeDescriptor) {
		QueryStringFormat queryStringFormat = new QueryStringFormat();
		if (conversionService != null) {
			queryStringFormat.setConversionService(conversionService);
		}
		return queryStringFormat.parseObject(body, targetTypeDescriptor);
	}

	@Override
	protected String toString(TypeDescriptor typeDescriptor, Object body, MimeType contentType) {
		QueryStringFormat queryStringFormat = new QueryStringFormat();
		if (conversionService != null) {
			queryStringFormat.setConversionService(conversionService);
		}
		queryStringFormat.setCharset(contentType.getCharset());
		return queryStringFormat.formatObject(body, typeDescriptor);
	}
}
