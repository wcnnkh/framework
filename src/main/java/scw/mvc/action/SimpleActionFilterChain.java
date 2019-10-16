package scw.mvc.action;

import java.util.Collection;
import java.util.Iterator;

import scw.core.utils.CollectionUtils;
import scw.mvc.Channel;

public class SimpleActionFilterChain implements ActionFilterChain {
	private Iterator<ActionFilter> iterator;

	public SimpleActionFilterChain(Collection<ActionFilter> filters) {
		if (!CollectionUtils.isEmpty(filters)) {
			this.iterator = filters.iterator();
		}
	}

	public Object doFilter(Action<Channel> action, Channel channel) throws Throwable {
		if (iterator == null) {
			return action.doAction(channel);
		}

		if (iterator.hasNext()) {
			return iterator.next().filter(action, channel, this);
		}

		return action.doAction(channel);
	}

}
