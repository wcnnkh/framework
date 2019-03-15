package scw.servlet.action;

import java.lang.reflect.Parameter;

import scw.servlet.Request;
import scw.servlet.Response;

public final class MethodParameter {
	private final Class<?> type;
	private final String name;
	private final Parameter parameter;

	public MethodParameter(Class<?> type, Parameter parameter, String name) {
		this.type = type;
		this.name = name;
		this.parameter = parameter;
	}

	public Object getParameter(Request request, Response response) throws Throwable {
		if (Request.class.isAssignableFrom(type)) {
			return request;
		} else if (Response.class.isAssignableFrom(type)) {
			return response;
		} else {
			return request.getParameter(parameter, name);
		}
	}

	public Parameter getParameter() {
		return parameter;
	}

	public Class<?> getType() {
		return type;
	}

	public String getName() {
		return name;
	}
}
