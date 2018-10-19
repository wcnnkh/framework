package shuchaowen.core.http.server;

public final class MethodParameter {
	private Class<?> type;
	private String name;

	public MethodParameter(Class<?> type, String name) {
		this.type = type;
		this.name = name;
	}

	public Object getParameter(Request request, Response response) throws Throwable {
		if (Request.class.isAssignableFrom(type)) {
			return request;
		} else if (Response.class.isAssignableFrom(type)) {
			return response;
		} else {
			return request.getParameter(type, name);
		}
	}
}
