package scw.net.http.server.mvc.context;

import scw.context.Context;
import scw.context.ContextExecute;
import scw.net.http.server.mvc.HttpChannel;
import scw.net.http.server.mvc.action.Action;
import scw.net.http.server.mvc.action.ActionService;

class ActionContextExecute implements ContextExecute<Object> {
	private HttpChannel httpChannel;
	private Action action;
	private ActionService service;

	public ActionContextExecute(HttpChannel httpChannel, Action action, ActionService service) {
		this.httpChannel = httpChannel;
		this.action = action;
		this.service = service;
	}

	public Object execute(Context context) throws Throwable {
		ContextManager.bindChannel(context, httpChannel);
		ContextManager.bindAction(context, action);
		return service.doAction(httpChannel, action);
	}
}
