package scw.mvc.support;

import scw.net.http.Method;

public interface HttpControllerConfig {
	String getController();

	String getClassController();

	String getMethodController();

	Method getHttpMethod();
}
