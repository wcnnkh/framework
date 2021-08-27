package io.basc.framework.mvc;

import io.basc.framework.core.annotation.AnnotationUtils;
import io.basc.framework.core.utils.ClassUtils;
import io.basc.framework.core.utils.CollectionUtils;
import io.basc.framework.logger.Levels;
import io.basc.framework.mvc.action.Action;
import io.basc.framework.mvc.annotation.LoggerEnable;
import io.basc.framework.util.MultiValueMap;
import io.basc.framework.web.ServerHttpRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public final class MVCUtils {
	private static final boolean SUPPORT_SERVLET = ClassUtils.isPresent("javax.servlet.Servlet", null);
	private static final String REQUEST_LOG_ID = "_scw_request_log_id";

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
	public static Levels getActionLoggerLevel(Action action) {
		LoggerEnable loggerEnable = AnnotationUtils.getAnnotation(LoggerEnable.class, action.getDeclaringClass(),
				action);
		return loggerEnable == null ? Levels.DEBUG : loggerEnable.value();
	}

	public static void setAction(ServerHttpRequest request, Action action) {
		request.setAttribute(Action.class.getName(), action);
	}

	public static Action getAction(ServerHttpRequest request) {
		return (Action) request.getAttribute(Action.class.getName());
	}
	
	public static void setRequestLogId(ServerHttpRequest request, String id) {
		request.setAttribute(REQUEST_LOG_ID, id);
	}
	
	public static String getRequestLogId(ServerHttpRequest request) {
		return (String) request.getAttribute(REQUEST_LOG_ID);
	}
}
