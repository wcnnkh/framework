package scw.mvc.context;

import scw.context.Context;
import scw.context.ContextExecute;
import scw.mvc.Channel;
import scw.mvc.action.Action;
import scw.mvc.action.filter.ActionFilterChain;

class FilterContextExecute implements ContextExecute<Object> {
	private Channel channel;
	private Action action;
	private ActionFilterChain chain;

	public FilterContextExecute(Channel channel, Action action,
			ActionFilterChain chain) {
		this.channel = channel;
		this.action = action;
		this.chain = chain;
	}

	public Object execute(Context context) throws Throwable {
		ContextManager.bindChannel(context, channel);
		ContextManager.bindAction(context, action);
		return chain.doFilter(channel, action);
	}
}
