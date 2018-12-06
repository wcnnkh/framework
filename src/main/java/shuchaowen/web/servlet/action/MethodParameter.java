package shuchaowen.web.servlet.action;

import shuchaowen.web.servlet.Request;
import shuchaowen.web.servlet.Response;

public final class MethodParameter {
	private final Class<?> type;
	private final String name;

	public MethodParameter(Class<?> type, String name) {
		this.type = type;
		this.name = name;
	}

	public Object getParameter(Request request, Response response) throws Throwable {
		if (Request.class.isAssignableFrom(type)) {
			return request;
		} else if (Request.class.isAssignableFrom(type)) {
			return response;
		} else {
			return request.getParameter(type, name);
		}
	}

	public Class<?> getType() {
		return type;
	}

	public String getName() {
		return name;
	}
}
