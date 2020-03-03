package scw.mvc.action.output.adapter;

import scw.mvc.Channel;
import scw.mvc.action.Action;
import scw.mvc.http.HttpChannel;

public abstract class HttpActionOutputAdapter implements ActionOutputAdapter {

	public final boolean isAdapter(Channel channel, Object obj) {
		if (obj == null) {
			return false;
		}

		if (channel instanceof HttpChannel) {
			return isAdapter((HttpChannel) channel, obj);
		}
		return false;
	}

	protected abstract boolean isAdapter(HttpChannel channel, Object obj);

	public final void output(Channel channel, Action action, Object obj)
			throws Throwable {
		output((HttpChannel) channel, action, obj);
	}

	protected abstract void output(HttpChannel channel, Action action,
			Object obj) throws Throwable;
}
