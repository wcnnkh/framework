package scw.mvc.support;

import scw.core.utils.XUtils;
import scw.net.http.Method;

public class SimpleHttpControllerConfig implements HttpControllerConfig {
	private final String classController;
	private final String methodController;
	private final Method httpMethod;
	private final String controller;

	public SimpleHttpControllerConfig(String classController, String methodController, Method httpMethod) {
		this.classController = XUtils.mergePath("/", classController);
		this.methodController = methodController;
		this.httpMethod = httpMethod;
		this.controller = XUtils.mergePath("/", classController, methodController);
	}

	public String getClassController() {
		return classController;
	}

	public String getMethodController() {
		return methodController;
	}

	public Method getHttpMethod() {
		return httpMethod;
	}

	public String getController() {
		return controller;
	}
}
