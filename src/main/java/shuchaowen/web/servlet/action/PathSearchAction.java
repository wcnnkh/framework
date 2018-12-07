package shuchaowen.web.servlet.action;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import shuchaowen.beans.BeanFactory;
import shuchaowen.common.exception.AlreadyExistsException;
import shuchaowen.common.utils.XUtils;
import shuchaowen.web.servlet.Request;
import shuchaowen.web.servlet.action.annotation.Controller;

public class PathSearchAction implements SearchAction {
	public static final String RESTURL_PATH_PARAMETER = "_resturl_path_parameter";
	private final List<RestUrlInfo> restUrlList = new ArrayList<RestUrlInfo>();
	private final Map<String, Map<String, Action>> actionMap = new HashMap<String, Map<String, Action>>();
	private boolean enableResutl = false;// 是否启用resturl
	private BeanFactory beanFactory;

	public PathSearchAction(BeanFactory beanFactory, boolean enableResutl) {
		this.enableResutl = enableResutl;
		this.beanFactory = beanFactory;
	}

	private Action getServletPathAndMethodAction(Request request) {
		Map<String, Action> map = actionMap.get(request.getPath());
		if (map == null) {
			return null;
		}

		return map.get(request.getMethod());
	}

	public Action getAction(Request request) throws Throwable {
		Action action = getServletPathAndMethodAction(request);
		if (action == null && enableResutl) {
			String[] pathArr = request.getPath().split("/");
			boolean isExist = false;
			Map<String, String> valueMap = null;
			for (RestUrlInfo restUrl : restUrlList) {
				if (pathArr.length != restUrl.getRegArr().length) {
					continue;
				}

				if (valueMap != null) {
					valueMap.clear();
				}

				for (int i = 0; i < restUrl.getRegArr().length; i++) {
					String str = restUrl.getRegArr()[i];
					if ("*".equals(str)) {
						if (valueMap == null) {
							valueMap = new HashMap<String, String>();
						}

						String key = restUrl.getKeyMap().get(i);
						valueMap.put(key, pathArr[i]);
					} else {
						if (pathArr[i].equals(restUrl.getRegArr()[i])) {
							isExist = true;
							break;
						}
					}
				}

				if (isExist) {
					action = getServletPathAndMethodAction(request);
					if (action == null) {
						return null;
					}

					request.setAttribute(RESTURL_PATH_PARAMETER, valueMap);
					return action;
				}
			}
		}
		return action;
	}
	
	private static String getExistActionErrMsg(Action action, Action oldAction){
		StringBuilder sb = new StringBuilder();
		sb.append("存在同样的controller[");
		sb.append(action.toString());
		sb.append("],原来的[");
		sb.append(oldAction.toString());
		sb.append("]");
		return sb.toString();
	}

	public void init(Collection<Class<?>> classList) throws Throwable {
		for (Class<?> clz : classList) {
			Controller clzController = clz.getAnnotation(Controller.class);
			if (clzController == null) {
				continue;
			}

			for (Method method : clz.getDeclaredMethods()) {
				Deprecated d = method.getAnnotation(Deprecated.class);
				if (d != null) {
					continue;
				}

				Controller methodController = method.getAnnotation(Controller.class);
				if (methodController == null) {
					continue;
				}

				String allPath = XUtils.mergePath("/", clzController.value(), methodController.value());
				String[] requestPathArr = allPath.split("/");
				Map<Integer, String> resultKeyMap = new HashMap<Integer, String>();
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

				Action action = new MethodAction(beanFactory, clz, method);
				RestUrlInfo restUrl = new RestUrlInfo();
				restUrl.setUrl(newRequestPath.toString());
				restUrl.setRegArr(newRequestPath.toString().split("/"));
				restUrl.setKeyMap(resultKeyMap);

				Map<String, Action> map = actionMap.get(allPath);
				if (map == null) {
					map = new HashMap<String, Action>();
				}

				shuchaowen.connection.http.enums.Method[] types = MethodAction.mergeRequestType(clz, method);
				for (shuchaowen.connection.http.enums.Method type : types) {
					if (map.containsKey(type.name())) {
						throw new AlreadyExistsException(getExistActionErrMsg(action, map.get(type.name())));
					}
					map.put(type.name(), action);
					actionMap.put(allPath, map);

					/**
					 * resturl
					 */
					if (resultKeyMap.size() > 0) {
						restUrlList.add(restUrl);
					}
				}
			}
		}
	}
}

final class RestUrlInfo {
	private String url;
	private Map<Integer, String> keyMap;
	private String[] regArr;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Map<Integer, String> getKeyMap() {
		return keyMap;
	}

	public void setKeyMap(Map<Integer, String> keyMap) {
		this.keyMap = keyMap;
	}

	public String[] getRegArr() {
		return regArr;
	}

	public void setRegArr(String[] regArr) {
		this.regArr = regArr;
	}
}
