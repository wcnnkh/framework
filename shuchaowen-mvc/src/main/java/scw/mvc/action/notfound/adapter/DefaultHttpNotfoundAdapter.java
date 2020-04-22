package scw.mvc.action.notfound.adapter;

import scw.core.instance.annotation.Configuration;
import scw.mvc.http.HttpChannel;

@Configuration(order = Integer.MIN_VALUE)
public class DefaultHttpNotfoundAdapter extends HttpNotFoundAdapter {

	@Override
	protected boolean isAdapter(HttpChannel httpChannel) {
		return true;
	}

	@Override
	protected HttpChannel notfoundInternal(HttpChannel channel)
			throws Throwable {
		channel.getResponse().sendError(404, "not found service");
		return null;
	}

}
