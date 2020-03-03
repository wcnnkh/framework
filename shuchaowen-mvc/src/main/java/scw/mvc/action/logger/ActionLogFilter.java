package scw.mvc.action.logger;

import java.util.Map;

import scw.beans.annotation.Configuration;
import scw.core.utils.StringUtils;
import scw.core.utils.SystemPropertyUtils;
import scw.json.JSONUtils;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.mvc.Channel;
import scw.mvc.action.Action;
import scw.mvc.action.filter.Filter;
import scw.mvc.action.filter.FilterChain;
import scw.mvc.http.HttpRequest;

@Configuration(order=Integer.MAX_VALUE)
public final class ActionLogFilter implements Filter {
	private static Logger logger = LoggerUtils
			.getLogger(ActionLogFilter.class);
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

		ActionLogConfig logConfig = action.getAnnotation(ActionLogConfig.class);
		if (logConfig != null && !logConfig.enable()) {
			return chain.doFilter(channel, action);
		}

		ActionLog log = null;
		try {
			log = logger(action, channel, logConfig);
		} catch (Exception e) {
			logger.error(e, "create log error: {}", channel.toString());
		}

		try {
			Object response = chain.doFilter(channel, action);
			if (log != null) {
				loggerResponse(logConfig, log, response);
			}
			return response;
		} catch (Throwable e) {
			if (log != null) {
				log.setErrorMessage(e.getMessage());
			}
			throw e;
		} finally {
			if (log != null) {
				log.setExecuteTime(System.currentTimeMillis()
						- channel.getCreateTime());
				logService.addLog(log);
			}
		}
	}

	protected ActionLog logger(Action action, Channel channel, ActionLogConfig logConfig)
			throws Exception {
		Map<String, String> attributeMap = actionLogFactory.getAttributeMap(
				action, channel);
		ActionLog log = new ActionLog();
		log.setAttributeMap(attributeMap);
		log.setController(action.getController());
		log.setIdentification(actionLogFactory.getIdentification(action, channel));
		log.setRequestController(channel.getRequest().getControllerPath());
		if (channel.getRequest() instanceof HttpRequest) {
			log.setHttpMethod(((HttpRequest) channel.getRequest()).getMethod());
		}

		log.setRequestContentType(channel.getRequest().getRawContentType());
		log.setRequestBody(channel.toString());
		return log;
	}

	private void loggerResponse(ActionLogConfig logConfig, ActionLog log, Object response) {
		try {
			if (logConfig != null && logConfig.response()) {
				log.setResponseContentType(log.getResponseContentType());
				log.setResponseBody(response == null ? null : JSONUtils
						.toJSONString(response));
			}
		} catch (Throwable e) {
			logger.error(e, "logger response error:{}",
					JSONUtils.toJSONString(log));
		}
	}
}
