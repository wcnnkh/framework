package scw.mvc.action.manager.support;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import scw.core.instance.annotation.Configuration;
import scw.lang.AlreadyExistsException;
import scw.mvc.MVCUtils;
import scw.mvc.action.manager.HttpAction;
import scw.mvc.action.manager.HttpAction.ControllerDescriptor;
import scw.mvc.action.manager.HttpActionLookup;
import scw.mvc.http.HttpChannel;
import scw.net.http.HttpMethod;

@Configuration(order=Integer.MIN_VALUE + 2)
public final class HttpPathActionLookup extends HttpActionLookup {
	private final Map<String, EnumMap<HttpMethod, HttpAction>> actionMap = new HashMap<String, EnumMap<HttpMethod, HttpAction>>();

	@Override
	public HttpAction lookup(HttpChannel httpChannel) {
		Map<HttpMethod, HttpAction> map = actionMap.get(httpChannel
				.getRequest().getControllerPath());
		if (map == null) {
			return null;
		}

		return map.get(httpChannel.getRequest().getMethod());
	}

	@Override
	public void register(HttpAction action) {
		for (ControllerDescriptor descriptor : action
				.getControllerDescriptors()) {
			if (descriptor.getRestful().isRestful()) {
				continue;
			}

			EnumMap<HttpMethod, HttpAction> map = actionMap.get(descriptor
					.getController());
			if (map == null) {
				map = new EnumMap<HttpMethod, HttpAction>(HttpMethod.class);
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
