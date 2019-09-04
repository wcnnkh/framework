package scw.mvc.servlet;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import scw.mvc.Channel;
import scw.mvc.Request;
import scw.mvc.RequestResponseModelChannel;
import scw.mvc.Response;
import scw.mvc.View;

public abstract class ServletView implements View {

	@SuppressWarnings("unchecked")
	public final void render(Channel channel) throws Throwable {
		if (channel instanceof RequestResponseModelChannel) {
			RequestResponseModelChannel<Request, Response> modelChannel = (RequestResponseModelChannel<Request, Response>) channel;
			Request request = modelChannel.getRequest();
			Response response = modelChannel.getResponse();
			if (request instanceof ServletRequest && response instanceof ServletResponse) {
				render(channel, (ServletRequest) request, (ServletResponse) response);
			}
		}
	}

	public abstract void render(Channel channel, ServletRequest request, ServletResponse servletResponse);
}
