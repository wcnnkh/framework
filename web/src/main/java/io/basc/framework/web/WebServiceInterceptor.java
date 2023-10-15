package io.basc.framework.web;

import java.io.IOException;

public interface WebServiceInterceptor {
	void intercept(ServerRequest serverRequest, ServerResponse serverResponse, WebService chain) throws IOException;
}
