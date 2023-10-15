package io.basc.framework.web;

import java.io.IOException;
import java.net.HttpCookie;

import io.basc.framework.http.HttpOutputMessage;
import io.basc.framework.http.HttpStatus;

public interface ServerHttpResponse extends HttpOutputMessage, ServerResponse {
	void addCookie(HttpCookie cookie);

	void addCookie(String name, String value);

	void sendError(int sc) throws IOException;

	void sendError(int sc, String msg) throws IOException;

	void sendRedirect(String location) throws IOException;

	void setStatusCode(HttpStatus httpStatus);

	void setStatus(int sc);

	int getStatus();
}
