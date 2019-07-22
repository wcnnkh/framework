package scw.servlet;

import java.io.Serializable;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import scw.core.exception.ParameterException;

public class ActionParameter implements Serializable{
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
			try {
				return request.getParameter(type, name);
			} catch (Exception e) {
				throw new ParameterException(e, "解析参数错误name=" + name + ",type=" + type.getName());
			}
		}
	}
}
