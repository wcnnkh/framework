package scw.servlet;

import java.io.Serializable;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class ActionParameter implements Serializable {
	private static final long serialVersionUID = 1L;
	private final Class<?> type;
	private final String name;

	public ActionParameter(Class<?> type, String name) {
		this.type = type;
		this.name = name;
	}

	public Object getParameter(Request request, ServletResponse response) {
		if (ServletRequest.class.isAssignableFrom(type)) {
			return request;
		} else if (ServletResponse.class.isAssignableFrom(type)) {
			return response;
		} else {
			return ServletUtils.getParameter(request, name, type);
		}
	}
}
