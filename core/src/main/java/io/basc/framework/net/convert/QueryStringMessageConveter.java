package io.basc.framework.net.convert;

import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.convert.config.ConversionServiceAware;
import io.basc.framework.http.MediaType;
import io.basc.framework.net.MimeType;
import io.basc.framework.text.query.QueryStringFormat;
import lombok.Setter;

@Setter
public class QueryStringMessageConveter extends StringMessageConverter<Object> implements ConversionServiceAware {
	private ConversionService conversionService;
	
	public QueryStringMessageConveter() {
		getMimeTypes().add(MediaType.APPLICATION_FORM_URLENCODED);
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
