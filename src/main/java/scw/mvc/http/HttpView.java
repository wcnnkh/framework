package scw.mvc.http;

import scw.core.exception.NotSupportException;
import scw.mvc.Channel;
import scw.mvc.View;

public abstract class HttpView implements View {

	protected void beforRender(HttpChannel channel, HttpRequest httpRequest, HttpResponse httpResponse)
			throws Throwable {
		// ignore
	}

	protected void afterRender(HttpChannel channel, HttpRequest httpRequest, HttpResponse httpResponse)
			throws Throwable {
		// ignore
	}

	public final void render(Channel channel) throws Throwable {
		if (channel instanceof HttpChannel) {
			HttpChannel httpChannel = (HttpChannel) channel;
			beforRender(httpChannel, httpChannel.getRequest(), httpChannel.getResponse());
			render(httpChannel, httpChannel.getRequest(), httpChannel.getResponse());
			afterRender(httpChannel, httpChannel.getRequest(), httpChannel.getResponse());
			return ;
		}
		throw new NotSupportException(channel.toString());
	}

	public abstract void render(HttpChannel channel, HttpRequest httpRequest, HttpResponse httpResponse)
			throws Throwable;
}
