package scw.mvc.action.logger;

import java.util.Map;

import scw.core.parameter.DefaultParameterDescriptor;
import scw.json.JSONUtils;
import scw.mvc.Channel;
import scw.mvc.Request;
import scw.mvc.action.Action;
import scw.mvc.action.logger.annotation.ActionLogConfig;
import scw.mvc.action.manager.HttpAction;
import scw.mvc.action.manager.HttpAction.ControllerDescriptor;
import scw.mvc.http.HttpRequest;

public abstract class AbstractActionLogFactory implements ActionLogFactory {
	protected abstract String getIdentification(Action action, Channel channel);

	protected abstract Map<String, String> getAttributeMap(Action action, Channel channel);

	protected String getAttirubteValue(Channel channel, String name) {
		return (String) channel.getParameter(new DefaultParameterDescriptor(name, String.class, String.class));
	}

	protected String getIp(Action action, Channel channel) {
		Request request = channel.getRequest();
		if (request instanceof HttpRequest) {
			return ((HttpRequest) request).getIP();
		}

		return null;
	}

	protected String getController(Channel channel, Action action) {
		if (action instanceof HttpAction) {
			for (ControllerDescriptor descriptor : ((HttpAction) action).getControllerDescriptors()) {
				return descriptor.getController();
			}
		}
		return null;
	}

	public ActionLog createActionLog(Action action, Channel channel, Object response, Throwable error) {
		ActionLogConfig logConfig = action.getAnnotatedElement().getAnnotation(ActionLogConfig.class);
		if (logConfig != null && !logConfig.enable()) {
			return null;
		}

		Map<String, String> attributeMap = getAttributeMap(action, channel);
		ActionLog log = new ActionLog();
		log.setAttributeMap(attributeMap);
		log.setController(getController(channel, action));
		log.setIdentification(getIdentification(action, channel));
		log.setRequestController(channel.getRequest().getController());
		if (channel.getRequest() instanceof HttpRequest) {
			log.setHttpMethod(((HttpRequest) channel.getRequest()).getMethod());
		}

		log.setRequestContentType(channel.getRequest().getRawContentType());
		log.setRequestBody(channel.toString());

		if (response != null) {
			setResponse(logConfig, action, channel, log, response);
		}

		if (error != null) {
			log.setErrorMessage(error.toString());
		}

		log.setExecuteTime(System.currentTimeMillis() - channel.getCreateTime());
		return log;
	}

	protected void setResponse(ActionLogConfig logConfig, Action action, Channel channel, ActionLog log,
			Object response) {
		try {
			if (logConfig != null && logConfig.response()) {
				log.setResponseContentType(log.getResponseContentType());
				log.setResponseBody(response == null ? null : JSONUtils.toJSONString(response));
			}
		} catch (Throwable e) {
			channel.getLogger().error(e, "logger response error:{}", JSONUtils.toJSONString(log));
		}
	}
}
