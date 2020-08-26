package scw.mvc.logger;

import scw.core.instance.annotation.Configuration;
import scw.mvc.HttpChannel;
import scw.mvc.action.Action;
import scw.mvc.action.ActionInterceptor;
import scw.mvc.action.ActionInterceptorChain;
import scw.mvc.action.ActionParameters;

@Configuration(order = Integer.MAX_VALUE)
public class ActionLogFilter implements ActionInterceptor {
	private ActionLogService actionLogService;
	private ActionLogFactory actionLogFactory;

	public ActionLogFilter(ActionLogService actionLogService, ActionLogFactory actionLogFactory) {
		this.actionLogFactory = actionLogFactory;
		this.actionLogService = actionLogService;
	}

	public Object intercept(HttpChannel httpChannel, Action action, ActionParameters parameters,
			ActionInterceptorChain chain) throws Throwable {
		ActionLog log = null;
		try {
			Object response = chain.intercept(httpChannel, action, parameters);
			log = actionLogFactory.createActionLog(action, httpChannel, response, null);
			return response;
		} catch (Throwable e) {
			log = actionLogFactory.createActionLog(action, httpChannel, null, e);
			throw e;
		} finally {
			if (log != null) {
				actionLogService.addLog(log);
			}
		}
	}
}
