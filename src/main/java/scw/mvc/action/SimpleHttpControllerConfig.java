package scw.mvc.action;

import scw.core.utils.StringUtils;
import scw.core.utils.XUtils;

public class SimpleHttpControllerConfig implements HttpControllerConfig {
	private final String classController;
	private final String methodController;
	private final String method;
	private final String controller;

	public SimpleHttpControllerConfig(String classController, String methodController, String method) {
		this.classController = classController;
		this.methodController = methodController;
		this.method = method;
		this.controller = XUtils.mergePath("/", classController, methodController);
	}

	public String getClassController() {
		return StringUtils.isEmpty(classController) ? "/" : classController;
	}

	public String getMethodController() {
		return methodController;
	}

	public String getMethod() {
		return method;
	}

	public String getController() {
		return controller;
	}
}
