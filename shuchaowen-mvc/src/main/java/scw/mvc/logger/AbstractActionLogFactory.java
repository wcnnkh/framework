package scw.mvc.logger;

import java.util.Map;

import scw.core.parameter.DefaultParameterDescriptor;
import scw.json.JSONUtils;
import scw.mvc.logger.annotation.ActionLogConfig;
import scw.net.http.server.ServerHttpRequest;
import scw.net.http.server.mvc.HttpChannel;
import scw.net.http.server.mvc.action.Action;
import scw.net.http.server.mvc.action.Action.ControllerDescriptor;

public abstract class AbstractActionLogFactory implements ActionLogFactory {
	protected abstract String getIdentification(Action action, HttpChannel httpChannel);

	protected abstract Map<String, String> getAttributeMap(Action action, HttpChannel httpChannel);

	protected String getAttirubteValue(HttpChannel httpChannel, String name) {
		return (String) httpChannel.getParameter(new DefaultParameterDescriptor(name, String.class, String.class));
	}

	protected String getController(HttpChannel httpChannel, Action action) {
		for (ControllerDescriptor descriptor : action.getControllerDescriptors()) {
			return descriptor.getController();
		}
		return null;
	}

	public ActionLog createActionLog(Action action, HttpChannel httpChannel, Object response, Throwable error) {
		ActionLogConfig logConfig = action.getAnnotatedElement().getAnnotation(ActionLogConfig.class);
		if (logConfig != null && !logConfig.enable()) {
			return null;
		}

		Map<String, String> attributeMap = getAttributeMap(action, httpChannel);
		ActionLog log = new ActionLog();
		log.setAttributeMap(attributeMap);
		log.setController(getController(httpChannel, action));
		log.setIdentification(getIdentification(action, httpChannel));
		log.setRequestController(httpChannel.getRequest().getController());
		if (httpChannel.getRequest() instanceof ServerHttpRequest) {
			log.setHttpMethod(((ServerHttpRequest) httpChannel.getRequest()).getMethod());
		}

		log.setRequestContentType(httpChannel.getRequest().getRawContentType());
		log.setRequestBody(httpChannel.toString());

		if (response != null) {
			setResponse(logConfig, action, httpChannel, log, response);
		}

		if (error != null) {
			log.setErrorMessage(error.toString());
		}

		log.setExecuteTime(System.currentTimeMillis() - httpChannel.getCreateTime());
		return log;
	}

	protected void setResponse(ActionLogConfig logConfig, Action action, HttpChannel httpChannel, ActionLog log,
			Object response) {
		try {
			if (logConfig != null && logConfig.response()) {
				log.setResponseContentType(log.getResponseContentType());
				log.setResponseBody(response == null ? null : JSONUtils.toJSONString(response));
			}
		} catch (Throwable e) {
			httpChannel.getLogger().error(e, "logger response error:{}", JSONUtils.toJSONString(log));
		}
	}
}
