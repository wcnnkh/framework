package scw.net.http.server.mvc.action;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import scw.beans.annotation.Bean;
import scw.core.instance.annotation.Configuration;
import scw.lang.AlreadyExistsException;
import scw.net.http.HttpMethod;
import scw.net.http.server.mvc.HttpChannel;
import scw.net.http.server.mvc.MVCUtils;
import scw.net.http.server.mvc.action.Action.ControllerDescriptor;

@Configuration(order = Integer.MIN_VALUE + 1)
@Bean(proxy = false)
public class HttpParameterActionLookup implements ActionLookup {
	private final Map<String, EnumMap<HttpMethod, Map<String, Action>>> actionMap = new HashMap<String, EnumMap<HttpMethod, Map<String, Action>>>();
	private String key = "action";

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Action lookup(HttpChannel httpChannel) {
		if (key == null) {
			return null;
		}

		Map<HttpMethod, Map<String, Action>> map = actionMap.get(httpChannel.getRequest().getController());
		if (map == null) {
			return null;
		}

		Map<String, Action> methodMap = map.get(httpChannel.getRequest().getMethod());
		if (methodMap == null) {
			return null;
		}

		String action = httpChannel.getString(key);
		if (action == null) {
			return null;
		}
		return methodMap.get(action);
	}

	protected void register(HttpMethod httpMethod, String classController, String methodController, Action action) {
		EnumMap<HttpMethod, Map<String, Action>> clzMap = actionMap.get(classController);
		if (clzMap == null) {
			clzMap = new EnumMap<HttpMethod, Map<String, Action>>(HttpMethod.class);
		}

		Map<String, Action> map = clzMap.get(httpMethod);
		if (map == null) {
			map = new HashMap<String, Action>();
		}

		if (map.containsKey(methodController)) {
			throw new AlreadyExistsException(MVCUtils.getExistActionErrMsg(action, map.get(methodController)));
		}

		map.put(methodController, action);
		clzMap.put(httpMethod, map);
		actionMap.put(classController, clzMap);
	}

	public void register(Action action) {
		for (ControllerDescriptor classControllerDescriptor : action.getTargetClassControllerDescriptors()) {
			if (classControllerDescriptor.getRestful().isRestful()) {
				for (ControllerDescriptor methodControllerDescriptor : action.getMethodControllerDescriptors()) {
					if (methodControllerDescriptor.getRestful().isRestful()) {
						continue;
					}

					for (ControllerDescriptor descriptor : action.getControllerDescriptors()) {
						HttpMethod httpMethod = descriptor.getHttpMethod();
						register(httpMethod, classControllerDescriptor.getController(),
								methodControllerDescriptor.getController(), action);
					}
				}
			}
		}
	}
}
