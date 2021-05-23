package scw.mvc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import scw.core.annotation.AnnotationUtils;
import scw.core.utils.ClassUtils;
import scw.core.utils.CollectionUtils;
import scw.logger.Level;
import scw.mvc.action.Action;
import scw.mvc.annotation.LoggerEnable;
import scw.util.MultiValueMap;
import scw.web.ServerHttpRequest;

public final class MVCUtils {
	private static final boolean SUPPORT_SERVLET = ClassUtils.isPresent("javax.servlet.Servlet", null);

	private MVCUtils() {
	};

	public static String getExistActionErrMsg(Action action, Action oldAction) {
		StringBuilder sb = new StringBuilder();
		sb.append("存在同样的controller[");
		sb.append(action.toString());
		sb.append("],原来的[");
		sb.append(oldAction.toString());
		sb.append("]");
		return sb.toString();
	}

	public static Map<String, String> getRequestParameterAndAppendValues(ServerHttpRequest request,
			CharSequence appendValueChars) {
		MultiValueMap<String, String> requestParams = request.getParameterMap();
		if (CollectionUtils.isEmpty(requestParams)) {
			return null;
		}

		Map<String, String> params = new HashMap<String, String>(requestParams.size(), 1);
		for (Entry<String, List<String>> entry : requestParams.entrySet()) {
			String name = entry.getKey();
			if (name == null) {
				continue;
			}

			List<String> values = entry.getValue();
			if (CollectionUtils.isEmpty(values)) {
				continue;
			}

			if (appendValueChars == null) {
				params.put(name, values.get(0));
			} else {
				StringBuilder sb = new StringBuilder();
				for (String value : values) {
					if (sb.length() != 0) {
						sb.append(appendValueChars);
					}

					sb.append(value);
				}
				params.put(name, sb.toString());
			}
		}
		return params;
	}

	/**
	 * 是否支持servlet
	 * 
	 * @return
	 */
	public static boolean isSupperServlet() {
		return SUPPORT_SERVLET;
	}

	/**
	 * 通过注解获取action可用的debug级别
	 * 
	 * @param action
	 * @return 如果不存在注解则返回空
	 */
	public static Level getActionLoggerLevel(Action action) {
		LoggerEnable loggerEnable = AnnotationUtils.getAnnotation(LoggerEnable.class, action.getDeclaringClass(),
				action);
		return loggerEnable == null ? null : loggerEnable.value();
	}

	public static void setAction(ServerHttpRequest request, Action action) {
		request.setAttribute(Action.class.getName(), action);
	}

	public static Action getAction(ServerHttpRequest request) {
		return (Action) request.getAttribute(Action.class.getName());
	}
}
