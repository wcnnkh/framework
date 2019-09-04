package scw.mvc.http.filter;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import scw.core.exception.AlreadyExistsException;
import scw.mvc.Action;
import scw.mvc.Channel;
import scw.mvc.MVCUtils;
import scw.mvc.annotation.Controller;
import scw.mvc.http.HttpRequest;
import scw.net.http.Method;

public final class RestServiceFilter extends AbstractHttpServiceFilter {
	private final EnumMap<Method, Map<String, RestInfo>> restMap = new EnumMap<Method, Map<String, RestInfo>>(
			Method.class);

	@Override
	public void scanning(Class<?> clz, java.lang.reflect.Method method, Controller classController,
			Controller methodController, Action<Channel> action) {
		RestInfo restUrlInfo = getRestInfo(action, clz, method);
		if (restUrlInfo == null) {
			return;
		}

		if (restUrlInfo.getKeyMap().size() == 0) {
			return;
		}

		/**
		 * resturl
		 */
		scw.net.http.Method[] types = MVCUtils.mergeRequestType(clz, method);
		for (scw.net.http.Method type : types) {
			Map<String, RestInfo> map = restMap.get(type);
			if (map == null) {
				map = new HashMap<String, RestInfo>();
			}

			if (map.containsKey(restUrlInfo.getUrl())) {
				throw new AlreadyExistsException(MVCUtils.getExistActionErrMsg(restUrlInfo.getAction(),
						map.get(restUrlInfo.getUrl()).getAction()));
			}

			map.put(restUrlInfo.getUrl(), restUrlInfo);
			restMap.put(type, map);
		}
	}

	@Override
	public Action<Channel> getAction(HttpRequest request) {
		Method method = Method.valueOf(request.getMethod());
		if (method == null) {
			return null;
		}

		Map<String, RestInfo> map = restMap.get(method);
		if (map == null) {
			return null;
		}

		String[] pathArr = request.getRequestPath().split("/");
		for (Entry<String, RestInfo> entry : map.entrySet()) {
			RestInfo restUrl = entry.getValue();
			if (pathArr.length != restUrl.getRegArr().length) {
				continue;
			}

			Map<String, String> valueMap = null;
			boolean find = true;
			for (int i = 0; i < restUrl.getRegArr().length; i++) {
				String str = restUrl.getRegArr()[i];
				if ("*".equals(str)) {
					if (valueMap == null) {
						valueMap = new HashMap<String, String>();
					}

					String key = restUrl.getKeyMap().get(i);
					valueMap.put(key, pathArr[i]);
				} else if (!pathArr[i].equals(str)) {
					find = false;
					break;
				}
			}

			if (find) {
				MVCUtils.setRestPathParameterMap(request, valueMap);
				return restUrl.getAction();
			}
		}
		return null;
	}
}
