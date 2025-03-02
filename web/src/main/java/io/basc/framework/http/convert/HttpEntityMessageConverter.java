package io.basc.framework.http.convert;

import io.basc.framework.core.convert.Source;
import io.basc.framework.http.HttpEntity;
import io.basc.framework.net.InputMessage;
import io.basc.framework.net.convert.support.AbstractEntityMessageConverter;
import lombok.NonNull;

@SuppressWarnings("rawtypes")
public class HttpEntityMessageConverter extends AbstractEntityMessageConverter<HttpEntity> {

	public HttpEntityMessageConverter() {
		super(HttpEntity.class);
	}

	@Override
	protected HttpEntity readToEntity(@NonNull Source body, @NonNull InputMessage message) {
		return new HttpEntity<>(body.any(), message.getHeaders());
	}
}
