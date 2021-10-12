package io.basc.framework.mvc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import io.basc.framework.mvc.action.Action;
import io.basc.framework.mvc.model.ModelAndView;
import io.basc.framework.util.ClassUtils;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.MultiValueMap;
import io.basc.framework.web.ServerHttpRequest;

public final class MVCUtils {
	private static final boolean SUPPORT_SERVLET = ClassUtils.isPresent("javax.servlet.Servlet", null);
	private static final String REQUEST_LOG_ID = "io.basc.framework.mvc.request.log.id";

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
	
	public static String getModelAndView(ServerHttpRequest request) {
		return (String) request.getAttribute(ModelAndView.class.getName());
	}
	
	public static void setModelAndView(ServerHttpRequest request, String view) {
		request.setAttribute(ModelAndView.class.getName(), view);
	}
}
