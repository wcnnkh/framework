package scw.mvc.action.notfound.adapter;

import scw.mvc.Channel;
import scw.mvc.http.HttpChannel;

public abstract class HttpNotFoundAdapter extends
		AbstractNotFoundAdapter<HttpChannel> {

	public final boolean isAdapter(Channel channel) {
		if (channel instanceof HttpChannel) {
			return true;
		}
		
		return isAdapter((HttpChannel) channel);
	}

	protected abstract boolean isAdapter(HttpChannel httpChannel);
}
