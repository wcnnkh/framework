package scw.net.http.server.mvc.context;

import scw.context.Context;
import scw.context.ContextExecute;
import scw.net.http.server.mvc.HttpChannel;
import scw.net.http.server.mvc.action.Action;
import scw.net.http.server.mvc.action.ActionFilter;
import scw.net.http.server.mvc.action.ActionService;

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
		ContextManager.bindChannel(context, httpChannel);
		ContextManager.bindAction(context, action);
		return filter.doFilter(httpChannel, action, actionService);
	}
}
