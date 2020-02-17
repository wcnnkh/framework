package scw.mvc.logger;

import java.util.Map;

import scw.beans.annotation.Configuration;
import scw.core.utils.StringUtils;
import scw.core.utils.SystemPropertyUtils;
import scw.json.JSONUtils;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.mvc.Action;
import scw.mvc.Channel;
import scw.mvc.FilterChain;
import scw.mvc.http.HttpRequest;
import scw.mvc.support.ActionFilter;

@Configuration
public final class LoggerActionFilter extends ActionFilter {
	private static Logger logger = LoggerUtils
			.getLogger(LoggerActionFilter.class);
	private static final boolean LOGGER_ENABLE = StringUtils.parseBoolean(
			SystemPropertyUtils.getProperty("mvc.logger.enable"), true);

	private LogService logService;
	private LoggerActionService loggerService;

	public LoggerActionFilter(LoggerActionService loggerService,
			LogService logService) {
		this.loggerService = loggerService;
		this.logService = logService;
	}

	@Override
	protected Object doFilter(Action action, Channel channel, FilterChain chain)
			throws Throwable {
		if (!LOGGER_ENABLE) {
			return chain.doFilter(channel);
		}

		LogConfig logConfig = action.getAnnotation(LogConfig.class);
		if (logConfig != null && !logConfig.enable()) {
			return chain.doFilter(channel);
		}

		Log log = null;
		try {
			log = logger(action, channel, logConfig);
		} catch (Exception e) {
			logger.error(e, "create log error: {}", channel.toString());
		}

		try {
			Object response = chain.doFilter(channel);
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

	protected Log logger(Action action, Channel channel, LogConfig logConfig)
			throws Exception {
		Map<String, String> attributeMap = loggerService.getAttributeMap(
				action, channel);
		Log log = new Log();
		log.setAttributeMap(attributeMap);
		log.setController(action.getController());
		log.setIdentification(loggerService.getIdentification(action, channel));
		log.setRequestController(channel.getRequest().getControllerPath());
		if (channel.getRequest() instanceof HttpRequest) {
			log.setHttpMethod(((HttpRequest) channel.getRequest()).getMethod());
		}

		log.setRequestContentType(channel.getRequest().getRawContentType());
		log.setRequestBody(channel.toString());
		return log;
	}

	private void loggerResponse(LogConfig logConfig, Log log, Object response) {
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
