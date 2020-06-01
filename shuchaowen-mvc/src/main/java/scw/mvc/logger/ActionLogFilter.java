package scw.mvc.logger;

import scw.beans.annotation.Bean;
import scw.core.instance.annotation.Configuration;
import scw.mvc.HttpChannel;
import scw.mvc.action.Action;
import scw.mvc.action.ActionFilter;

@Configuration(order = Integer.MAX_VALUE)
@Bean(proxy = false)
public class ActionLogFilter implements ActionFilter {
	private ActionLogService actionLogService;
	private ActionLogFactory actionLogFactory;

	public ActionLogFilter(ActionLogService actionLogService,
			ActionLogFactory actionLogFactory) {
		this.actionLogFactory = actionLogFactory;
		this.actionLogService = actionLogService;
	}

	public Object doFilter(Action action, HttpChannel httpChannel) throws Throwable {
		ActionLog log = null;
		try {
			Object response = action.doAction(httpChannel);
			log = actionLogFactory.createActionLog(action, httpChannel, response,
					null);
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
