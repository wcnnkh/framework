package scw.mvc.logger;

import scw.beans.annotation.Bean;
import scw.core.instance.annotation.Configuration;
import scw.net.http.server.mvc.HttpChannel;
import scw.net.http.server.mvc.action.Action;
import scw.net.http.server.mvc.action.ActionFilter;
import scw.net.http.server.mvc.action.ActionService;

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

	public Object doFilter(HttpChannel httpChannel, Action action,
			ActionService service) throws Throwable {
		ActionLog log = null;
		try {
			Object response = service.doAction(httpChannel, action);
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
