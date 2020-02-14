package scw.mvc.support;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import scw.lang.AlreadyExistsException;
import scw.mvc.MVCUtils;
import scw.mvc.http.HttpChannel;
import scw.mvc.support.action.HttpAction;
import scw.net.http.Method;

public final class HttpPathActionFactory extends HttpActionFactory {
	private final Map<String, EnumMap<Method, HttpAction>> actionMap = new HashMap<String, EnumMap<Method, HttpAction>>();

	@Override
	public HttpAction getAction(HttpChannel httpChannel) {
		Map<Method, HttpAction> map = actionMap.get(httpChannel.getRequest()
				.getControllerPath());
		if (map == null) {
			return null;
		}

		return map.get(httpChannel.getRequest().getMethod());
	}

	@Override
	public void scanning(HttpAction action) {
		HttpRestfulInfo httpRestfulInfo = HttpRestfulInfo.getRestInfo(action);
		if (httpRestfulInfo == null) {
			return;
		}

		if (httpRestfulInfo.getKeyMap().size() > 0) {
			return;
		}

		EnumMap<Method, HttpAction> map = actionMap.get(action.getController());
		if (map == null) {
			map = new EnumMap<Method, HttpAction>(Method.class);
		}

		for (Method method : action.getHttpMethods()) {
			if (map.containsKey(method)) {
				throw new AlreadyExistsException(MVCUtils.getExistActionErrMsg(
						action, map.get(method)));
			}
			map.put(method, action);
			actionMap.put(action.getController(), map);
		}
	}
}
