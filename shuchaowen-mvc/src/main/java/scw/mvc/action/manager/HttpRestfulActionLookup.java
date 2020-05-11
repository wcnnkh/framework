package scw.mvc.action.manager;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import scw.beans.annotation.Bean;
import scw.core.instance.annotation.Configuration;
import scw.core.utils.StringUtils;
import scw.lang.AlreadyExistsException;
import scw.mvc.MVCUtils;
import scw.mvc.action.manager.HttpAction.ControllerDescriptor;
import scw.mvc.http.HttpChannel;
import scw.net.Restful;
import scw.net.Restful.RestfulMatchingResult;
import scw.net.http.HttpMethod;

@Configuration(order=Integer.MIN_VALUE)
@Bean(proxy=false)
public class HttpRestfulActionLookup extends HttpActionLookup {
	private final EnumMap<HttpMethod, Map<Restful, HttpAction>> restMap = new EnumMap<HttpMethod, Map<Restful, HttpAction>>(
			HttpMethod.class);

	@Override
	public void register(HttpAction action) {
		for (ControllerDescriptor descriptor : action
				.getControllerDescriptors()) {
			if (!descriptor.getRestful().isRestful()) {
				continue;
			}

			Map<Restful, HttpAction> map = restMap.get(descriptor
					.getHttpMethod());
			if (map == null) {
				map = new HashMap<Restful, HttpAction>();
			}

			if (map.containsKey(descriptor.getRestful())) {
				throw new AlreadyExistsException(MVCUtils.getExistActionErrMsg(
						action, map.get(descriptor.getRestful())));
			}

			map.put(descriptor.getRestful(), action);
			restMap.put(descriptor.getHttpMethod(), map);
		}
	}

	@Override
	public HttpAction lookup(HttpChannel channel) {
		Map<Restful, HttpAction> map = restMap.get(channel.getRequest()
				.getMethod());
		if (map == null) {
			return null;
		}

		String[] pathArr = StringUtils.split(channel.getRequest().getController(), '/');
		for (Entry<Restful, HttpAction> entry : map.entrySet()) {
			Restful restful = entry.getKey();
			RestfulMatchingResult result = restful.matching(pathArr);
			if (result.isSuccess()) {
				MVCUtils.setRestfulParameterMap(channel,
						result.getParameterMap());
				return entry.getValue();
			}
		}
		return null;
	}
}
