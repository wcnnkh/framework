package scw.mvc.action.notfound.adapter;

import java.util.LinkedList;

import scw.lang.NotSupportedException;
import scw.mvc.Channel;

public class MultiNotfoundAdapter extends LinkedList<NotFoundAdapter> implements
		NotFoundAdapter {
	private static final long serialVersionUID = 1L;

	public boolean isAdapter(Channel channel) {
		for (NotFoundAdapter adapter : this) {
			if (adapter.isAdapter(channel)) {
				return true;
			}
		}
		return false;
	}

	public Object notfound(Channel channel) throws Throwable {
		for (NotFoundAdapter adapter : this) {
			if (adapter.isAdapter(channel)) {
				return adapter.notfound(channel);
			}
		}
		throw new NotSupportedException(
				"not support NotFoundAdapter in channel:" + channel.toString());
	}

}
