package scw.mvc.action;

import scw.core.context.Context;
import scw.core.context.ContextExecute;
import scw.mvc.Channel;
import scw.mvc.Filter;
import scw.mvc.FilterChain;
import scw.mvc.MVCUtils;

public abstract class ActionService implements Filter {

	public abstract Action getAction(Channel channel);

	public final Object doFilter(final Channel channel, FilterChain chain) throws Throwable {
		Action action = getAction(channel);
		if (action == null) {
			return chain.doFilter(channel);
		}

		return MVCUtils.execute(new Execute(channel, action));
	}

	final class Execute implements ContextExecute<Object> {
		private final Channel channel;
		private final Action action;

		public Execute(Channel channel, Action action) {
			this.channel = channel;
			this.action = action;
		}

		public Object execute(Context context) throws Throwable {
			context.bindResource(Action.class, action);
			return action.doAction(channel);
		}
	}
}
