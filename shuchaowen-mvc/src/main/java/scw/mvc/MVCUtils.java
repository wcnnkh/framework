package scw.mvc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import scw.beans.BeanUtils;
import scw.core.GlobalPropertyFactory;
import scw.core.parameter.DefaultParameterDescriptor;
import scw.core.parameter.ParameterDescriptor;
import scw.core.parameter.ParameterUtils;
import scw.core.utils.ClassUtils;
import scw.core.utils.CollectionUtils;
import scw.http.server.ServerHttpRequest;
import scw.lang.ParameterException;
import scw.mvc.action.Action;
import scw.util.MultiValueMap;

public final class MVCUtils {
	private static final boolean SUPPORT_SERVLET = ClassUtils.isPresent("javax.servlet.Servlet");

	private MVCUtils() {
	};

	public static Object[] getParameterValues(HttpChannel httpChannel, ParameterDescriptor[] parameterDescriptors)
			throws ParameterException {
		Object[] args = new Object[parameterDescriptors.length];
		for (int i = 0; i < parameterDescriptors.length; i++) {
			ParameterDescriptor parameterDescriptor = parameterDescriptors[i];
			parameterDescriptor = new DefaultParameterDescriptor(ParameterUtils.getDisplayName(parameterDescriptor),
					parameterDescriptor);
			try {
				args[i] = httpChannel.getParameter(parameterDescriptor);
			} catch (Exception e) {
				throw new ParameterException("Parameter error [" + parameterDescriptor.getName() + "]", e);
			}
		}
		return args;
	}

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

	public static String getScanAnnotationPackageName() {
		return GlobalPropertyFactory.getInstance().getValue("scw.scan.mvc.package", String.class,
				BeanUtils.getScanAnnotationPackageName());
	}
}
