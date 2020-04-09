package scw.mvc.handler;

import scw.beans.annotation.Configuration;
import scw.mvc.http.HttpChannel;
import scw.net.http.HttpMethod;

@Configuration(order=Integer.MIN_VALUE)
public final class LastHttpHandler extends HttpHandler{
	
	@Override
	protected Object doHttpHandler(HttpChannel channel, HandlerChain chain)
			throws Throwable {
		if (HttpMethod.OPTIONS == channel.getRequest().getMethod()) {
			return chain.doHandler(channel);
		}

		channel.getLogger().warn("not found：{}", channel.toString());
		channel.getResponse().sendError(404, "not found handler");
		return null;
	}

}
