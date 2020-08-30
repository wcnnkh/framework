package scw.mvc;

import java.io.IOException;

import scw.beans.BeanFactory;
import scw.http.server.ServerHttpRequest;
import scw.http.server.ServerHttpResponse;
import scw.json.JSONSupport;
import scw.json.JSONUtils;

public class DefaultHttpChannelFactory implements HttpChannelFactory {
	protected final BeanFactory beanFactory;
	private JSONSupport jsonSupport;

	public DefaultHttpChannelFactory(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	public final JSONSupport getJsonSupport() {
		return jsonSupport == null? JSONUtils.getJsonSupport():jsonSupport;
	}

	public void setJsonSupport(JSONSupport jsonSupport) {
		this.jsonSupport = jsonSupport;
	}

	public HttpChannel create(ServerHttpRequest request, ServerHttpResponse response) throws IOException {
		return new DefaultHttpChannel(beanFactory, getJsonSupport(), request, response);
	}
}
