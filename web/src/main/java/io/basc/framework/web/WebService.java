package io.basc.framework.web;

import java.io.IOException;

@FunctionalInterface
public interface WebService {
	void service(ServerRequest serverRequest, ServerResponse serverResponse) throws IOException, WebException;
}
