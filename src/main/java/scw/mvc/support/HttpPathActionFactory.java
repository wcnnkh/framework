package scw.mvc.support;

import java.util.HashMap;
import java.util.Map;

import scw.core.exception.AlreadyExistsException;
import scw.mvc.MVCUtils;
import scw.mvc.http.HttpChannel;

public final class HttpPathActionFactory extends HttpActionFactory {
	private final Map<String, Map<String, HttpAction>> actionMap = new HashMap<String, Map<String, HttpAction>>();

	@Override
	public HttpAction getAction(HttpChannel httpChannel) {
		Map<String, HttpAction> map = actionMap.get(httpChannel.getRequest().getRequestPath());
		if (map == null) {
			return null;
		}

		return map.get(httpChannel.getRequest().getMethod());
	}

	@Override
	public void scanning(HttpAction action, HttpControllerConfig httpControllerConfig) {
		HttpRestfulInfo httpRestfulInfo = HttpRestfulInfo.getRestInfo(action, httpControllerConfig);
		if (httpRestfulInfo == null) {
			return;
		}

		if (httpRestfulInfo.getKeyMap().size() > 0) {
			return;
		}

		Map<String, HttpAction> map = actionMap.get(httpControllerConfig.getController());
		if (map == null) {
			map = new HashMap<String, HttpAction>();
		}

		if (map.containsKey(httpControllerConfig.getMethod())) {
			throw new AlreadyExistsException(
					MVCUtils.getExistActionErrMsg(action, map.get(httpControllerConfig.getMethod())));
		}
		map.put(httpControllerConfig.getMethod(), action);
		actionMap.put(httpControllerConfig.getController(), map);
	}
}
