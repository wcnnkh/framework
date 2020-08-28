package scw.mvc;

import java.io.IOException;

import scw.beans.BeanFactory;
import scw.http.server.ServerHttpRequest;
import scw.http.server.ServerHttpResponse;
import scw.json.JSONSupport;

public class DefaultHttpChannelFactory implements HttpChannelFactory {
	protected final BeanFactory beanFactory;
	private final JSONSupport jsonSupport;

	public DefaultHttpChannelFactory(BeanFactory beanFactory, JSONSupport jsonSupport) {
		this.beanFactory = beanFactory;
		this.jsonSupport = jsonSupport;
	}

	public final JSONSupport getJsonSupport() {
		return jsonSupport;
	}

	public HttpChannel create(ServerHttpRequest request, ServerHttpResponse response) throws IOException {
		return new DefaultHttpChannel(beanFactory, jsonSupport, request, response);
	}
}
