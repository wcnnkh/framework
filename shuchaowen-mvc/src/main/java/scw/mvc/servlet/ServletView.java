package scw.mvc.servlet;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import scw.mvc.Channel;
import scw.mvc.Request;
import scw.mvc.Response;
import scw.mvc.View;

public abstract class ServletView implements View {

	public final void render(Channel channel) throws Throwable {
		Request request = channel.getRequest();
		Response response = channel.getResponse();
		if (request instanceof ServletRequest && response instanceof ServletResponse) {
			render(channel, (ServletRequest) request, (ServletResponse) response);
		}
	}

	public abstract void render(Channel channel, ServletRequest request, ServletResponse servletResponse);
}
