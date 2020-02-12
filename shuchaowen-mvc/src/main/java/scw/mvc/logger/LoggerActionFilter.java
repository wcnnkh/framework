package scw.mvc.logger;

import scw.beans.annotation.Configuration;
import scw.core.annotation.DefaultValue;
import scw.core.annotation.ParameterName;
import scw.core.parameter.ParameterConfig;
import scw.core.parameter.SimpleParameterConfig;
import scw.core.utils.StringUtils;
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

	private LogService<?> logService;
	private ParameterConfig identificationParameterConfig;

	public LoggerActionFilter(
			@ParameterName("mvc.logger.identification") @DefaultValue("uid") String identificationKey,
			LogService<?> logService) {
		if (StringUtils.isNotEmpty(identificationKey)) {
			this.identificationParameterConfig = new SimpleParameterConfig(
					identificationKey, null, String.class, String.class);
		}
		this.logService = logService;
	}

	@Override
	protected Object doFilter(Action action, Channel channel, FilterChain chain)
			throws Throwable {
		LogConfig logConfig = action.getAnnotation(LogConfig.class);
		if (logConfig != null && !logConfig.enable()) {
			return chain.doFilter(channel);
		}

		String identification = null;
		if (identificationParameterConfig != null) {
			identification = (String) channel
					.getParameter(identificationParameterConfig);
		}

		Log log = new Log();
		log.setIdentification(identification);
		log.setController(channel.getRequest().getControllerPath());
		if (channel.getRequest() instanceof HttpRequest) {
			log.setHttpMethod(((HttpRequest) channel.getRequest())
					.getRawMethod());
		}

		log.setRequestContentType(channel.getRequest().getRawContentType());
		log.setRequestBody(channel.toString());
		try {
			Object response = chain.doFilter(channel);
			loggerResponse(logConfig, log, response);
			return response;
		} catch (Throwable e) {
			log.setErrorMessage(e.getMessage());
			throw e;
		} finally {
			logService.addLog(log);
		}
	}

	private void loggerResponse(LogConfig logConfig, Log log, Object response) {
		try {
			if (logConfig != null && logConfig.response()) {
				log.setResponseContentType(log.getResponseContentType());
				log.setResponseBody(response == null ? null : JSONUtils
						.toJSONString(response));
			}
		} catch (Throwable e) {
			if (logger.isErrorEnabled()) {
				logger.error(e, "logger response error:{}",
						JSONUtils.toJSONString(log));
			} else {
				e.printStackTrace();
			}
		}
	}
}
