package run.soeasy.framework.http.convert;

import lombok.NonNull;
import run.soeasy.framework.core.convert.Source;
import run.soeasy.framework.http.HttpEntity;
import run.soeasy.framework.net.InputMessage;
import run.soeasy.framework.net.convert.support.AbstractEntityMessageConverter;

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
