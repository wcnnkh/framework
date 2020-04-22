package scw.mvc.service.context;

import scw.context.Context;
import scw.context.ContextExecute;
import scw.mvc.Channel;
import scw.mvc.action.Action;
import scw.mvc.action.filter.ActionFilterChain;

class ActionFilterContextExecute implements ContextExecute<Object> {
	private Channel channel;
	private Action action;
	private ActionFilterChain chain;

	public ActionFilterContextExecute(Channel channel, Action action,
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
