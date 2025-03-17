package run.soeasy.framework.http.client;

import java.io.IOException;

import run.soeasy.framework.http.HttpOutputMessage;
import run.soeasy.framework.http.HttpRequest;
import run.soeasy.framework.net.client.ClientRequest;

public interface ClientHttpRequest extends HttpOutputMessage, HttpRequest, ClientRequest {
	public static interface ClientHttpRequestWrapper<W extends ClientHttpRequest>
			extends ClientHttpRequest, HttpOutputMessageWrapper<W>, HttpRequestWrapper<W>, ClientRequestWrapper<W> {
		@Override
		default ClientHttpResponse execute() throws IOException {
			return getSource().execute();
		}

		@Override
		default ClientHttpRequest buffered() {
			return getSource().buffered();
		}
	}

	ClientHttpResponse execute() throws IOException;

	public static class BufferingClientHttpRequest<W extends ClientHttpRequest> extends BufferingHttpOutputMessage<W>
			implements ClientHttpRequestWrapper<W> {

		public BufferingClientHttpRequest(W source) {
			super(source);
		}

		@Override
		public ClientHttpRequest buffered() {
			return this;
		}
	}

	default ClientHttpRequest buffered() {
		return new BufferingClientHttpRequest<>(this);
	}
}
