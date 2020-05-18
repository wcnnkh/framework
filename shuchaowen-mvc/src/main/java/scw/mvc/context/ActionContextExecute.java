package scw.mvc.context;

import scw.context.Context;
import scw.context.ContextExecute;
import scw.mvc.HttpChannel;
import scw.mvc.action.Action;
import scw.mvc.action.ActionService;

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
		RequestContextManager.bindChannel(context, httpChannel);
		RequestContextManager.bindAction(context, action);
		return service.doAction(httpChannel, action);
	}
}
