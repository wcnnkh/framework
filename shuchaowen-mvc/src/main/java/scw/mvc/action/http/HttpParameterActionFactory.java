package scw.mvc.action.http;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import scw.lang.AlreadyExistsException;
import scw.mvc.MVCUtils;
import scw.mvc.action.AnnotationAction;
import scw.mvc.http.HttpChannel;
import scw.net.http.HttpMethod;

public final class HttpParameterActionFactory extends HttpActionFactory {
	private final Map<String, EnumMap<HttpMethod, Map<String, HttpAction>>> actionMap = new HashMap<String, EnumMap<HttpMethod, Map<String, HttpAction>>>();
	private String key;

	public HttpParameterActionFactory(String key) {
		this.key = key;
	}

	@Override
	public HttpAction getAction(HttpChannel httpChannel) {
		if (key == null) {
			return null;
		}

		Map<HttpMethod, Map<String, HttpAction>> map = actionMap.get(httpChannel
				.getRequest().getControllerPath());
		if (map == null) {
			return null;
		}

		Map<String, HttpAction> methodMap = map.get(httpChannel.getRequest()
				.getMethod());
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
	public void scanning(HttpAction action) {
		if (!(action instanceof AnnotationAction)) {
			return;
		}

		AnnotationAction annotationAction = (AnnotationAction) action;
		for (HttpMethod method : action.getHttpMethods()) {
			EnumMap<HttpMethod, Map<String, HttpAction>> clzMap = actionMap
					.get(annotationAction.getClassController());
			if (clzMap == null) {
				clzMap = new EnumMap<HttpMethod, Map<String, HttpAction>>(
						HttpMethod.class);
			}

			Map<String, HttpAction> map = clzMap.get(method);
			if (map == null) {
				map = new HashMap<String, HttpAction>();
			}

			if (map.containsKey(annotationAction.getMethodController())) {
				throw new AlreadyExistsException(MVCUtils.getExistActionErrMsg(
						action,
						map.get(annotationAction.getMethodController())));
			}

			map.put(annotationAction.getMethodController(), action);
			clzMap.put(method, map);
			actionMap.put(annotationAction.getClassController(), clzMap);
		}
	}
}
