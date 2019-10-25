package scw.mvc.action;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import scw.core.exception.AlreadyExistsException;
import scw.mvc.MVCUtils;
import scw.mvc.http.HttpChannel;

public final class HttpParameterActionService extends HttpActionService {
	private final Map<String, Map<String, Map<String, HttpAction>>> actionMap = new HashMap<String, Map<String, Map<String, HttpAction>>>();
	private String key;

	public HttpParameterActionService(Collection<ActionFilter> actionFilters, String key) {
		super(actionFilters);
		this.key = key;
	}

	@Override
	public HttpAction getAction(HttpChannel httpChannel) {
		if (key == null) {
			return null;
		}

		Map<String, Map<String, HttpAction>> map = actionMap.get(httpChannel.getRequest().getRequestPath());
		if (map == null) {
			return null;
		}

		Map<String, HttpAction> methodMap = map.get(httpChannel.getRequest().getMethod());
		if (methodMap == null) {
			return null;
		}

		String action = httpChannel.getString(key);
		if (action == null) {
			return null;
		}
		return methodMap.get(action);
	}

	@Override
	public void scanning(HttpAction action, HttpControllerConfig controllerConfig) {
		Map<String, Map<String, HttpAction>> clzMap = actionMap.get(controllerConfig.getClassController());
		if (clzMap == null) {
			clzMap = new HashMap<String, Map<String, HttpAction>>();
		}

		Map<String, HttpAction> map = clzMap.get(controllerConfig.getMethod());
		if (map == null) {
			map = new HashMap<String, HttpAction>();
		}

		if (map.containsKey(controllerConfig.getMethodController())) {
			throw new AlreadyExistsException(
					MVCUtils.getExistActionErrMsg(action, map.get(controllerConfig.getMethodController())));
		}

		map.put(controllerConfig.getMethodController(), action);
		clzMap.put(controllerConfig.getMethod(), map);
		actionMap.put(controllerConfig.getClassController(), clzMap);
	}
}
