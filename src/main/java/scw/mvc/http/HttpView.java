package scw.mvc.http;

import scw.mvc.Channel;
import scw.mvc.View;

public abstract class HttpView implements View {

	public final void render(Channel channel) throws Throwable {
		if (channel instanceof HttpChannel) {
			render((HttpChannel) channel, (HttpRequest) ((HttpChannel) channel).getRequest(),
					(HttpResponse) ((HttpChannel) channel).getResponse());
		}
	}

	public abstract void render(HttpChannel channel, HttpRequest httpRequest,
			HttpResponse httpResponse) throws Throwable;
}
