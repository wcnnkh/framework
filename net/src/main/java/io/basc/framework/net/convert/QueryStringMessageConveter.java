package io.basc.framework.net.convert;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.config.ConversionService;
import io.basc.framework.core.convert.config.ConversionServiceAware;
import io.basc.framework.core.mapping.text.query.QueryStringFormat;
import io.basc.framework.net.MimeType;
import io.basc.framework.net.MimeTypeUtils;
import lombok.Setter;

@Setter
public class QueryStringMessageConveter extends StringMessageConverter<Object> implements ConversionServiceAware {
	private ConversionService conversionService;

	public QueryStringMessageConveter() {
		getMimeTypes().add(MimeTypeUtils.APPLICATION_FORM_URLENCODED);
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
