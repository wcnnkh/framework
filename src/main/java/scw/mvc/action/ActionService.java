package scw.mvc.action;

import scw.core.context.Context;
import scw.mvc.Channel;
import scw.mvc.Filter;
import scw.mvc.FilterChain;
import scw.mvc.MVCUtils;

public abstract class ActionService implements Filter {

	public abstract Action<Channel> getAction(Channel channel);

	public Object doFilter(Channel channel, FilterChain chain) throws Throwable {
		Action<Channel> action = getAction(channel);
		if (action == null) {
			return chain.doFilter(channel);
		}

		Context context = MVCUtils.getContext();
		if (context != null) {
			context.bindResource(channel, action);
		}
		return action.doAction(channel);
	}

}
