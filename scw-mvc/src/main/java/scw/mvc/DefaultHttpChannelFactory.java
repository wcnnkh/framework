package scw.mvc;

import java.io.IOException;

import scw.beans.BeanFactory;
import scw.http.server.ServerHttpRequest;
import scw.http.server.ServerHttpResponse;
import scw.json.JSONSupport;
import scw.json.JSONUtils;

public class DefaultHttpChannelFactory implements HttpChannelFactory {
	protected final BeanFactory beanFactory;
	private JSONSupport jsonSupport = JSONUtils.getJsonSupport();

	public DefaultHttpChannelFactory(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	public JSONSupport getJsonSupport() {
		return jsonSupport;
	}

	public void setJsonSupport(JSONSupport jsonSupport) {
		this.jsonSupport = jsonSupport;
	}

	public HttpChannel create(ServerHttpRequest request, ServerHttpResponse response) throws IOException {
		return new DefaultHttpChannel<ServerHttpRequest, ServerHttpResponse>(beanFactory, getJsonSupport(), request,
				response);
	}
}
