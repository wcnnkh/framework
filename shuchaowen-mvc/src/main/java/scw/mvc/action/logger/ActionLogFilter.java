package scw.mvc.action.logger;

import scw.beans.BeanFactory;
import scw.beans.annotation.Bean;
import scw.core.instance.annotation.Configuration;
import scw.mvc.Channel;
import scw.mvc.action.Action;
import scw.mvc.action.filter.ActionFilter;
import scw.mvc.action.filter.ActionFilterChain;
import scw.util.value.property.PropertyFactory;

@Configuration(order = Integer.MAX_VALUE)
@Bean(proxy = false)
public class ActionLogFilter implements ActionFilter {
	private ActionLogService logService;
	private ActionLogFactory actionLogFactory;

	public ActionLogFilter(BeanFactory beanFactory,
			PropertyFactory propertyFactory) {
		if (propertyFactory.getValue("mvc.action.logger.filter.enable",
				boolean.class, true)
				&& beanFactory.isInstance(ActionLogService.class)
				&& beanFactory.isInstance(ActionLogFactory.class)) {
			this.logService = beanFactory.getInstance(ActionLogService.class);
			this.actionLogFactory = beanFactory
					.getInstance(ActionLogFactory.class);
		}
	}

	public Object doFilter(Channel channel, Action action,
			ActionFilterChain chain) throws Throwable {
		if (logService == null || actionLogFactory == null) {
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
