package scw.mvc.action.logger;

import scw.core.GlobalPropertyFactory;
import scw.core.instance.annotation.Configuration;
import scw.mvc.Channel;
import scw.mvc.action.Action;
import scw.mvc.action.filter.ActionFilter;
import scw.mvc.action.filter.ActionFilterChain;

@Configuration(order = Integer.MAX_VALUE)
public final class ActionLogFilter implements ActionFilter {
	private static final boolean LOGGER_ENABLE = GlobalPropertyFactory
			.getInstance().getValue("mvc.logger.enable", boolean.class, true);
	private ActionLogService logService;
	private ActionLogFactory actionLogFactory;

	public ActionLogFilter(ActionLogFactory actionLogFactory,
			ActionLogService logService) {
		this.actionLogFactory = actionLogFactory;
		this.logService = logService;
	}

	public Object doFilter(Channel channel, Action action, ActionFilterChain chain)
			throws Throwable {
		if (!LOGGER_ENABLE) {
			return chain.doFilter(channel, action);
		}

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
				logService.addLog(log);
			}
		}
	}
}
