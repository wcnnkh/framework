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

@Configuration(order=Integer.MIN_VALUE + 2)
@Bean(proxy=false)
public class HttpPathActionLookup implements ActionLookup {
	private final Map<String, EnumMap<HttpMethod, Action>> actionMap = new HashMap<String, EnumMap<HttpMethod, Action>>();

	public Action lookup(HttpChannel httpChannel) {
		Map<HttpMethod, Action> map = actionMap.get(httpChannel
				.getRequest().getPath());
		if (map == null) {
			return null;
		}

		return map.get(httpChannel.getRequest().getMethod());
	}

	public void register(Action action) {
		for (ControllerDescriptor descriptor : action
				.getControllerDescriptors()) {
			if (descriptor.getRestful().isRestful()) {
				continue;
			}

			EnumMap<HttpMethod, Action> map = actionMap.get(descriptor
					.getController());
			if (map == null) {
				map = new EnumMap<HttpMethod, Action>(HttpMethod.class);
			}

			if (map.containsKey(descriptor.getHttpMethod())) {
				throw new AlreadyExistsException(MVCUtils.getExistActionErrMsg(
						action, map.get(descriptor.getHttpMethod())));
			}
			map.put(descriptor.getHttpMethod(), action);
			actionMap.put(descriptor.getController(), map);
		}
	}
}
