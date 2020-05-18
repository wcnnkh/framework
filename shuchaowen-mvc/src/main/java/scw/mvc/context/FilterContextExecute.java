package scw.mvc.context;

import scw.context.Context;
import scw.context.ContextExecute;
import scw.mvc.HttpChannel;
import scw.mvc.action.Action;
import scw.mvc.action.ActionFilter;
import scw.mvc.action.ActionService;

class FilterContextExecute implements ContextExecute<Object> {
	private HttpChannel httpChannel;
	private Action action;
	private ActionFilter filter;
	private ActionService actionService;

	public FilterContextExecute(HttpChannel httpChannel, Action action, ActionFilter filter,
			ActionService actionService) {
		this.httpChannel = httpChannel;
		this.action = action;
		this.filter = filter;
		this.actionService = actionService;
	}

	public Object execute(Context context) throws Throwable {
		RequestContextManager.bindChannel(context, httpChannel);
		RequestContextManager.bindAction(context, action);
		return filter.doFilter(httpChannel, action, actionService);
	}
}
