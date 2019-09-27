package scw.mvc.action;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import scw.core.exception.AlreadyExistsException;
import scw.mvc.MVCUtils;
import scw.mvc.http.HttpChannel;

public final class HttpPathService extends HttpActionService {
	private final Map<String, Map<String, HttpAction>> actionMap = new HashMap<String, Map<String, HttpAction>>();

	public HttpPathService(Collection<ActionFilter> actionFilters) {
		super(actionFilters);
	}

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
		HttpRestInfo httpRestInfo = HttpRestInfo.getRestInfo(action, httpControllerConfig);
		if (httpRestInfo == null) {
			return;
		}

		if (httpRestInfo.getKeyMap().size() > 0) {
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
