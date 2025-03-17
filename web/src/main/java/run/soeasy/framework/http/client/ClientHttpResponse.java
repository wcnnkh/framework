package run.soeasy.framework.http.client;

import java.io.IOException;

import run.soeasy.framework.http.HttpInputMessage;
import run.soeasy.framework.http.HttpStatus;
import run.soeasy.framework.net.client.ClientResponse;

public interface ClientHttpResponse extends HttpInputMessage, ClientResponse {
	public static interface ClientHttpResponseWrapper<W extends ClientHttpResponse>
			extends ClientHttpResponse, HttpInputMessageWrapper<W>, ClientResponseWrapper<W> {
		@Override
		default HttpStatus getStatusCode() throws IOException {
			return getSource().getStatusCode();
		}

		@Override
		default int getRawStatusCode() throws IOException {
			return getSource().getRawStatusCode();
		}

		@Override
		default String getStatusText() throws IOException {
			return getSource().getStatusText();
		}

		@Override
		default void close() {
			getSource().close();
		}

		@Override
		default ClientHttpResponse buffered() {
			return getSource().buffered();
		}
	}

	/**
	 * Return the HTTP status code of the response.
	 * 
	 * @return the HTTP status as an HttpStatus enum value
	 * @throws IOException              in case of I/O errors
	 * @throws IllegalArgumentException in case of an unknown HTTP status code
	 * @see HttpStatus#valueOf(int)
	 */
	default HttpStatus getStatusCode() throws IOException {
		return HttpStatus.valueOf(getRawStatusCode());
	}

	int getRawStatusCode() throws IOException;

	String getStatusText() throws IOException;

	void close();

	public static class BufferingClientHttpResponse<W extends ClientHttpResponse> extends BufferingHttpInputMessage<W>
			implements ClientHttpResponseWrapper<W> {

		public BufferingClientHttpResponse(W source) {
			super(source);
		}

		@Override
		public ClientHttpResponse buffered() {
			return this;
		}
	}

	default ClientHttpResponse buffered() {
		return new BufferingClientHttpResponse<>(this);
	}
}
