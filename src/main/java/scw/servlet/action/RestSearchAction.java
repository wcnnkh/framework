package scw.servlet.action;

import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import scw.beans.BeanFactory;
import scw.common.exception.AlreadyExistsException;
import scw.common.utils.XUtils;
import scw.net.http.enums.Method;
import scw.servlet.Request;
import scw.servlet.action.annotation.Controller;

public class RestSearchAction implements SearchAction {
	public static final String RESTURL_PATH_PARAMETER = "_resturl_path_parameter";
	private final EnumMap<Method, Map<String, RestInfo>> restMap = new EnumMap<Method, Map<String, RestInfo>>(
			Method.class);
	private final BeanFactory beanFactory;

	public RestSearchAction(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	public Action getAction(Request request) throws Exception {
		Method method = Method.valueOf(request.getMethod());
		if (method == null) {
			return null;
		}

		Map<String, RestInfo> map = restMap.get(method);
		if (map == null) {
			return null;
		}

		String[] pathArr = request.getServletPath().split("/");
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
				request.setAttribute(RESTURL_PATH_PARAMETER, valueMap);
				return restUrl.getAction();
			}
		}
		return null;
	}

	public static RestInfo getRestInfo(BeanFactory beanFactory, Class<?> clz, java.lang.reflect.Method method) {
		Controller clzController = clz.getAnnotation(Controller.class);
		if (clzController == null) {
			return null;
		}

		Deprecated d = method.getAnnotation(Deprecated.class);
		if (d != null) {
			return null;
		}

		Controller methodController = method.getAnnotation(Controller.class);
		if (methodController == null) {
			return null;
		}

		String allPath = XUtils.mergePath("/", clzController.value(), methodController.value());
		String[] requestPathArr = allPath.split("/");
		Map<Integer, String> resultKeyMap = new HashMap<Integer, String>(4);
		StringBuilder newRequestPath = new StringBuilder(allPath.length());
		for (int i = 0; i < requestPathArr.length; i++) {
			String str = requestPathArr[i];
			if (str.startsWith("{") && str.endsWith("}")) {
				newRequestPath.append("*");
				resultKeyMap.put(i, str.substring(1, str.length() - 1));
			} else {
				newRequestPath.append(str);
			}

			if (i < requestPathArr.length - 1) {
				newRequestPath.append("/");
			}
		}

		if (allPath.endsWith("/")) {
			newRequestPath.append("/");
		}

		RestInfo restUrl = new RestInfo();
		restUrl.setUrl(newRequestPath.toString());
		restUrl.setRegArr(newRequestPath.toString().split("/"));
		restUrl.setKeyMap(resultKeyMap);
		restUrl.setAction(new MethodAction(beanFactory, clz, method));
		return restUrl;
	}

	public void init(Collection<Class<?>> classList) throws Exception {
		for (Class<?> clz : classList) {
			Controller clzController = clz.getAnnotation(Controller.class);
			if (clzController == null) {
				continue;
			}

			for (java.lang.reflect.Method method : clz.getDeclaredMethods()) {
				RestInfo restUrlInfo = getRestInfo(beanFactory, clz, method);
				if (restUrlInfo == null) {
					continue;
				}

				if (restUrlInfo.getKeyMap().size() == 0) {
					continue;
				}

				/**
				 * resturl
				 */
				scw.net.http.enums.Method[] types = MethodAction.mergeRequestType(clz, method);
				for (scw.net.http.enums.Method type : types) {
					Map<String, RestInfo> map = restMap.get(type);
					if (map == null) {
						map = new HashMap<String, RestInfo>();
					}

					if (map.containsKey(restUrlInfo.getUrl())) {
						throw new AlreadyExistsException(PathSearchAction.getExistActionErrMsg(restUrlInfo.getAction(),
								map.get(restUrlInfo.getUrl()).getAction()));
					}

					map.put(restUrlInfo.getUrl(), restUrlInfo);
					restMap.put(type, map);
				}
			}
		}
	}
}
