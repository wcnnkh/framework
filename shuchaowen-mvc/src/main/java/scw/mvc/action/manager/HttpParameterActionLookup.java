package scw.mvc.action.manager;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import scw.beans.annotation.Bean;
import scw.core.instance.annotation.Configuration;
import scw.lang.AlreadyExistsException;
import scw.mvc.MVCUtils;
import scw.mvc.action.manager.HttpAction.ControllerDescriptor;
import scw.mvc.http.HttpChannel;
import scw.net.http.HttpMethod;

@Configuration(order=Integer.MIN_VALUE + 1)
@Bean(proxy=false)
public class HttpParameterActionLookup extends HttpActionLookup {
	private final Map<String, EnumMap<HttpMethod, Map<String, HttpAction>>> actionMap = new HashMap<String, EnumMap<HttpMethod, Map<String, HttpAction>>>();
	private String key = "action";

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	@Override
	public HttpAction lookup(HttpChannel httpChannel) {
		if (key == null) {
			return null;
		}

		Map<HttpMethod, Map<String, HttpAction>> map = actionMap
				.get(httpChannel.getRequest().getController());
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

	protected void register(HttpMethod httpMethod, String classController,
			String methodController, HttpAction action) {
		EnumMap<HttpMethod, Map<String, HttpAction>> clzMap = actionMap
				.get(classController);
		if (clzMap == null) {
			clzMap = new EnumMap<HttpMethod, Map<String, HttpAction>>(
					HttpMethod.class);
		}

		Map<String, HttpAction> map = clzMap.get(httpMethod);
		if (map == null) {
			map = new HashMap<String, HttpAction>();
		}

		if (map.containsKey(methodController)) {
			throw new AlreadyExistsException(MVCUtils.getExistActionErrMsg(
					action, map.get(methodController)));
		}

		map.put(methodController, action);
		clzMap.put(httpMethod, map);
		actionMap.put(classController, clzMap);
	}

	@Override
	protected void register(HttpAction action) {
		for (ControllerDescriptor classControllerDescriptor : action
				.getTargetClassControllerDescriptors()) {
			if (classControllerDescriptor.getRestful().isRestful()) {
				for (ControllerDescriptor methodControllerDescriptor : action
						.getMethodControllerDescriptors()) {
					if (methodControllerDescriptor.getRestful().isRestful()) {
						continue;
					}

					for (ControllerDescriptor descriptor : action
							.getControllerDescriptors()) {
						HttpMethod httpMethod = descriptor.getHttpMethod();
						register(httpMethod,
								classControllerDescriptor.getController(),
								methodControllerDescriptor.getController(),
								action);
					}
				}
			}
		}
	}
}
