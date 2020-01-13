package scw.http.client;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import scw.http.HttpHeaders;
import scw.http.HttpMethod;
import scw.http.HttpRequest;
import scw.http.StreamingHttpOutputMessage;
import scw.util.StreamUtils;

class InterceptingClientHttpRequest extends AbstractBufferingClientHttpRequest {

	private final ClientHttpRequestFactory requestFactory;

	private final List<ClientHttpRequestInterceptor> interceptors;

	private HttpMethod method;

	private URI uri;

	protected InterceptingClientHttpRequest(ClientHttpRequestFactory requestFactory,
			List<ClientHttpRequestInterceptor> interceptors, URI uri, HttpMethod method) {

		this.requestFactory = requestFactory;
		this.interceptors = interceptors;
		this.method = method;
		this.uri = uri;
	}

	public HttpMethod getMethod() {
		return this.method;
	}

	public URI getURI() {
		return this.uri;
	}

	@Override
	protected final ClientHttpResponse executeInternal(HttpHeaders headers, byte[] bufferedOutput) throws IOException {
		InterceptingRequestExecution requestExecution = new InterceptingRequestExecution();
		return requestExecution.execute(this, bufferedOutput);
	}

	private class InterceptingRequestExecution implements ClientHttpRequestExecution {

		private final Iterator<ClientHttpRequestInterceptor> iterator;

		public InterceptingRequestExecution() {
			this.iterator = interceptors.iterator();
		}

		public ClientHttpResponse execute(HttpRequest request, final byte[] body) throws IOException {
			if (this.iterator.hasNext()) {
				ClientHttpRequestInterceptor nextInterceptor = this.iterator.next();
				return nextInterceptor.intercept(request, body, this);
			} else {
				ClientHttpRequest delegate = requestFactory.createRequest(request.getURI(), request.getMethod());
				for (Map.Entry<String, List<String>> entry : request.getHeaders().entrySet()) {
					List<String> values = entry.getValue();
					for (String value : values) {
						delegate.getHeaders().add(entry.getKey(), value);
					}
				}
				if (body.length > 0) {
					if (delegate instanceof StreamingHttpOutputMessage) {
						StreamingHttpOutputMessage streamingOutputMessage = (StreamingHttpOutputMessage) delegate;
						streamingOutputMessage.setBody(new StreamingHttpOutputMessage.Body() {
							public void writeTo(final OutputStream outputStream) throws IOException {
								StreamUtils.copy(body, outputStream);
							}
						});
					} else {
						StreamUtils.copy(body, delegate.getBody());
					}
				}
				return delegate.execute();
			}
		}
	}

}
