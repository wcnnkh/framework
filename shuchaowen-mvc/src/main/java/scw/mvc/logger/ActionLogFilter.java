package scw.mvc.logger;

import scw.core.instance.annotation.Configuration;
import scw.mvc.HttpChannel;
import scw.mvc.action.Action;
import scw.mvc.action.ActionFilter;
import scw.mvc.action.ActionFilterChain;

@Configuration(order = Integer.MAX_VALUE)
public class ActionLogFilter implements ActionFilter {
	private ActionLogService actionLogService;
	private ActionLogFactory actionLogFactory;

	public ActionLogFilter(ActionLogService actionLogService, ActionLogFactory actionLogFactory) {
		this.actionLogFactory = actionLogFactory;
		this.actionLogService = actionLogService;
	}

	public Object doFilter(HttpChannel httpChannel, Action action, Object[] args, ActionFilterChain filterChain)
			throws Throwable {
		ActionLog log = null;
		try {
			Object response = filterChain.doFilter(httpChannel, action, args);
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
