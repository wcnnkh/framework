package scw.mvc.action.http;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import scw.lang.AlreadyExistsException;
import scw.mvc.MVCUtils;
import scw.mvc.http.HttpChannel;
import scw.net.http.HttpMethod;

public final class HttpPathActionFactory extends HttpActionFactory {
	private final Map<String, EnumMap<HttpMethod, HttpAction>> actionMap = new HashMap<String, EnumMap<HttpMethod, HttpAction>>();

	@Override
	public HttpAction getAction(HttpChannel httpChannel) {
		Map<HttpMethod, HttpAction> map = actionMap.get(httpChannel.getRequest()
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

		EnumMap<HttpMethod, HttpAction> map = actionMap.get(action.getController());
		if (map == null) {
			map = new EnumMap<HttpMethod, HttpAction>(HttpMethod.class);
		}

		for (HttpMethod method : action.getHttpMethods()) {
			if (map.containsKey(method)) {
				throw new AlreadyExistsException(MVCUtils.getExistActionErrMsg(
						action, map.get(method)));
			}
			map.put(method, action);
			actionMap.put(action.getController(), map);
		}
	}
}
