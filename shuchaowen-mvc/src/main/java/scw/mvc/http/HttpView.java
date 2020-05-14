package scw.mvc.http;

import scw.mvc.Channel;
import scw.mvc.View;
import scw.net.http.server.ServerHttpRequest;
import scw.net.http.server.ServerHttpResponse;

public abstract class HttpView implements View {

	protected void beforRender(Channel channel, ServerHttpRequest serverHttpRequest,
			ServerHttpResponse serverHttpResponse) throws Throwable {
		// ignore
	}

	protected void afterRender(Channel channel, ServerHttpRequest serverHttpRequest,
			ServerHttpResponse serverHttpResponse) throws Throwable {
		// ignore
	}

	public final void render(Channel channel) throws Throwable {
		beforRender(channel, channel.getRequest(), channel.getResponse());
		render(channel, channel.getRequest(), channel.getResponse());
		afterRender(channel, channel.getRequest(), channel.getResponse());
		return;
	}

	public abstract void render(Channel channel, ServerHttpRequest serverHttpRequest,
			ServerHttpResponse serverHttpResponse) throws Throwable;
}
