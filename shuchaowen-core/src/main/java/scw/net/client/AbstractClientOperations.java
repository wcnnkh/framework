package scw.net.client;

import java.io.IOException;
import java.lang.reflect.Type;

import scw.io.IOUtils;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.net.MimeType;
import scw.net.NetworkUtils;
import scw.net.client.http.exception.HttpClientException;
import scw.net.message.InputMessage;
import scw.net.message.SerializableInputMessage;
import scw.net.message.converter.MultiMessageConverter;
import scw.net.message.converter.support.AllMessageConverter;

public abstract class AbstractClientOperations<B extends ClientRequestBuilder<R>, R extends ClientRequest, P extends ClientResponse>
		implements ClientOperations<B, R, P> {
	protected final transient Logger logger = LoggerUtils.getLogger(getClass());
	private MultiMessageConverter messageConverter = new MultiMessageConverter();

	public AbstractClientOperations() {
		messageConverter.add(new AllMessageConverter());
	}

	public MultiMessageConverter getMessageConverter() {
		return messageConverter;
	}

	@SuppressWarnings("unchecked")
	public <T> T execute(B builder, ClientRequestCallback<R> requestCallback,
			ClientResponseExtractor<P, T> clientResponseExtractor)
			throws HttpClientException {
		P response = null;
		R request;
		try {
			request = builder.builder();
			requestCallback(builder, request, requestCallback);
			response = (P) request.execute();
			handleResponse(builder, response);
			return responseExtractor(builder, response, clientResponseExtractor);
		} catch (IOException ex) {
			throw throwIOException(ex, builder);
		} finally {
			if (response != null) {
				response.close();
			}
		}
	}

	protected void requestCallback(B builder, R request,
			ClientRequestCallback<R> requestCallback) throws IOException {
		if (requestCallback != null) {
			requestCallback.callback(request);
		}
	}

	protected <T> T responseExtractor(B builder, P response,
			ClientResponseExtractor<P, T> clientResponseExtractor)
			throws IOException {
		return clientResponseExtractor == null ? null : clientResponseExtractor
				.execute(response);
	}

	protected abstract RuntimeException throwIOException(IOException ex,
			B builder);

	protected abstract void handleResponse(B builder, P response)
			throws IOException;

	public SerializableInputMessage execute(final B builder,
			final InputMessage inputMessage) {
		return execute(builder, new ClientRequestCallback<R>() {

			public void callback(R clientRequest) throws IOException {
				NetworkUtils.writeHeader(inputMessage, clientRequest);
				IOUtils.write(inputMessage.getBody(), clientRequest.getBody());
			}
		}, new ClientResponseExtractor<P, SerializableInputMessage>() {

			public SerializableInputMessage execute(P response)
					throws IOException {
				return convertToSerializableInputMessage(builder, response);
			}
		});
	}

	protected abstract SerializableInputMessage convertToSerializableInputMessage(
			B builder, P response) throws IOException;

	public Object execute(B builder, final Type responseType, final Object body,
			final MimeType contentType) {
		return execute(builder, body == null? null:new ClientRequestCallback<R>() {

			public void callback(R clientRequest) throws IOException {
				getMessageConverter().write(body, contentType,
						clientRequest);
			}
		}, new ClientResponseExtractor<P, Object>() {

			public Object execute(P response) throws IOException {
				return getMessageConverter().read(responseType, response);
			}
		});
	}

	public <T> T execute(B builder, final Class<? extends T> responseType,
			final Object body, final MimeType contentType) {
		return execute(builder, new ClientRequestCallback<R>() {

			public void callback(R clientRequest) throws IOException {
				getMessageConverter().write(body, contentType,
						clientRequest);
			}
		}, new ClientResponseExtractor<P, T>() {

			@SuppressWarnings("unchecked")
			public T execute(P response) throws IOException {
				return (T) getMessageConverter().read(responseType, response);
			}
		});
	}
}
