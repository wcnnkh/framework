package scw.net.client;

import java.lang.reflect.Type;

import scw.net.MimeType;
import scw.net.message.InputMessage;
import scw.net.message.SerializableInputMessage;

public interface ClientOperations<B extends ClientRequestBuilder<R>, R extends ClientRequest, P extends ClientResponse> {
	<T> T execute(B builder, ClientRequestCallback<R> requestCallback,
			ClientResponseExtractor<P, T> clientResponseExtractor);

	SerializableInputMessage execute(B builder, InputMessage inputMessage);

	<T> T execute(B builder, Class<? extends T> responseType, Object body,
			MimeType contentType);

	Object execute(B builder, Type responseType, Object body, MimeType contentType);
}
