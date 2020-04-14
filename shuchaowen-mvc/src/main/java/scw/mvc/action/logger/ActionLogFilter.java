package scw.mvc.action.logger;

import scw.beans.annotation.Bean;
import scw.core.instance.annotation.Configuration;
import scw.mvc.Channel;
import scw.mvc.action.Action;
import scw.mvc.action.filter.ActionFilter;
import scw.mvc.action.filter.ActionFilterChain;

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

	public Object doFilter(Channel channel, Action action,
			ActionFilterChain chain) throws Throwable {
		ActionLog log = null;
		try {
			Object response = chain.doFilter(channel, action);
			log = actionLogFactory.createActionLog(action, channel, response,
					null);
			return response;
		} catch (Throwable e) {
			log = actionLogFactory.createActionLog(action, channel, null, e);
			throw e;
		} finally {
			if (log != null) {
				actionLogService.addLog(log);
			}
		}
	}
}
