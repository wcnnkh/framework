package run.soeasy.framework.http.client;

import java.io.IOException;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.convert.TargetDescriptor;
import run.soeasy.framework.http.HttpRequest;
import run.soeasy.framework.http.HttpStatus;
import run.soeasy.framework.net.convert.MessageConverter;

@RequiredArgsConstructor
public class MessageConverterClientHttpResponseExtractor<T> implements ClientHttpResponseExtractor<T> {
	@NonNull
	private final MessageConverter messageConverter;
	@NonNull
	private final TargetDescriptor targetDescriptor;

	@SuppressWarnings("unchecked")
	@Override
	public T execute(HttpRequest request, ClientHttpResponse response) throws IOException {
		if (!request.getMethod().hasResponseBody()) {
			return null;
		}

		if (HttpStatus.OK.getCode() != response.getRawStatusCode()) {
			return null;
		}

		return (T) messageConverter.readFrom(targetDescriptor, response.getContentType(), response, response);
	}

}
