package scw.mvc.handler;

import scw.beans.annotation.Configuration;
import scw.mvc.http.HttpChannel;
import scw.net.http.Method;

@Configuration(order=Integer.MIN_VALUE)
public final class LastHttpHandler extends HttpHandler{
	
	@Override
	protected void doHttpHandler(HttpChannel channel, HandlerChain chain)
			throws Throwable {
		if (Method.OPTIONS == channel.getRequest().getMethod()) {
			return ;
		}

		channel.getLogger().warn("not found：{}", channel.toString());
		channel.getResponse().sendError(404, "not found handler");
	}

}
