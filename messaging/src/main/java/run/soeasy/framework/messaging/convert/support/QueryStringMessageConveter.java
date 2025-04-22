package run.soeasy.framework.messaging.convert.support;

import java.io.IOException;
import java.nio.charset.Charset;

import lombok.Getter;
import lombok.Setter;
import run.soeasy.framework.core.convert.value.Data;
import run.soeasy.framework.core.convert.value.Writeable;
import run.soeasy.framework.messaging.MediaType;

@Getter
@Setter
public class QueryStringMessageConveter extends TextMessageConverter {
	public QueryStringMessageConveter() {
		getMediaTypeRegistry().clear();
		getMediaTypeRegistry().add(MediaType.APPLICATION_FORM_URLENCODED);
	}

	@Override
	protected Object parseObject(String body, Writeable targetDescriptor) {
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
