package scw.mvc.logger;

import java.util.Map;

import scw.core.annotation.AnnotationUtils;
import scw.http.server.HttpControllerDescriptor;
import scw.http.server.ServerHttpRequest;
import scw.json.JSONUtils;
import scw.mvc.HttpChannel;
import scw.mvc.action.Action;
import scw.mvc.logger.annotation.ActionLogConfig;

public abstract class AbstractActionLogFactory implements ActionLogFactory {
	protected abstract String getIdentification(Action action, HttpChannel httpChannel);

	protected abstract Map<String, String> getAttributeMap(Action action, HttpChannel httpChannel);

	protected String getAttirubteValue(HttpChannel httpChannel, String name) {
		return httpChannel.getValue(name).getAsString();
	}

	protected String getController(HttpChannel httpChannel, Action action) {
		for (HttpControllerDescriptor descriptor : action.getHttpControllerDescriptors()) {
			return descriptor.getPath();
		}
		return null;
	}

	public ActionLog createActionLog(Action action, HttpChannel httpChannel, Object response, Throwable error) {
		ActionLogConfig logConfig = AnnotationUtils.getAnnotation(ActionLogConfig.class, action.getSourceClass(),
				action.getAnnotatedElement());
		if (logConfig != null && !logConfig.enable()) {
			return null;
		}

		Map<String, String> attributeMap = getAttributeMap(action, httpChannel);
		ActionLog log = new ActionLog();
		log.setAttributeMap(attributeMap);
		log.setController(getController(httpChannel, action));
		log.setIdentification(getIdentification(action, httpChannel));
		log.setRequestController(httpChannel.getRequest().getPath());
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
