package scw.mvc.support;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import scw.lang.AlreadyExistsException;
import scw.mvc.MVCUtils;
import scw.mvc.http.HttpChannel;
import scw.net.http.Method;

public final class HttpPathActionFactory extends HttpActionFactory {
	private final Map<String, EnumMap<Method, HttpAction>> actionMap = new HashMap<String, EnumMap<Method, HttpAction>>();

	@Override
	public HttpAction getAction(HttpChannel httpChannel) {
		Map<Method, HttpAction> map = actionMap.get(httpChannel.getRequest().getControllerPath());
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

		EnumMap<Method, HttpAction> map = actionMap.get(httpControllerConfig.getController());
		if (map == null) {
			map = new EnumMap<Method, HttpAction>(Method.class);
		}

		if (map.containsKey(httpControllerConfig.getHttpMethod())) {
			throw new AlreadyExistsException(
					MVCUtils.getExistActionErrMsg(action, map.get(httpControllerConfig.getHttpMethod())));
		}
		map.put(httpControllerConfig.getHttpMethod(), action);
		actionMap.put(httpControllerConfig.getController(), map);
	}
}
