package scw.mvc.action.logger;

import scw.beans.annotation.Configuration;
import scw.core.utils.StringUtils;
import scw.core.utils.SystemPropertyUtils;
import scw.mvc.Channel;
import scw.mvc.action.Action;
import scw.mvc.action.filter.Filter;
import scw.mvc.action.filter.FilterChain;

@Configuration(order=Integer.MAX_VALUE)
public final class ActionLogFilter implements Filter {
	private static final boolean LOGGER_ENABLE = StringUtils.parseBoolean(
			SystemPropertyUtils.getProperty("mvc.logger.enable"), true);
	private ActionLogService logService;
	private ActionLogFactory actionLogFactory;

	public ActionLogFilter(ActionLogFactory actionLogFactory,
			ActionLogService logService) {
		this.actionLogFactory = actionLogFactory;
		this.logService = logService;
	}
	
	public Object doFilter(Channel channel, Action action, FilterChain chain)
			throws Throwable {
		if (!LOGGER_ENABLE) {
			return chain.doFilter(channel, action);
		}
		
		ActionLog log = null;
		try {
			Object response = chain.doFilter(channel, action);
			log = actionLogFactory.createActionLog(action, channel, response, null);
			return response;
		} catch (Throwable e) {
			log = actionLogFactory.createActionLog(action, channel, null, e);
			throw e;
		}finally{
			if(log != null){
				logService.addLog(log);
			}
		}
	}
}
