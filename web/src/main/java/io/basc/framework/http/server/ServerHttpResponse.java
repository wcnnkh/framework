package io.basc.framework.http.server;

import java.io.IOException;
import java.net.HttpCookie;

import io.basc.framework.http.HttpOutputMessage;
import io.basc.framework.http.HttpStatus;
import io.basc.framework.net.server.ServerResponse;

public interface ServerHttpResponse extends HttpOutputMessage, ServerResponse {
	@FunctionalInterface
	public static interface ServerHttpResponseWrapper<W extends ServerHttpResponse>
			extends ServerHttpResponse, HttpOutputMessageWrapper<W>, ServerResponseWrapper<W> {
		@Override
		default void addCookie(HttpCookie cookie) {
			getSource().addCookie(cookie);
		}

		@Override
		default void addCookie(String name, String value) {
			getSource().addCookie(name, value);
		}

		@Override
		default void sendError(int sc) throws IOException {
			getSource().sendError(sc);
		}

		@Override
		default void sendError(int sc, String msg) throws IOException {
			getSource().sendError(sc, msg);
		}

		@Override
		default void sendRedirect(String location) throws IOException {
			getSource().sendRedirect(location);
		}

		@Override
		default void setStatus(int sc) {
			getSource().setStatus(sc);
		}

		@Override
		default void setStatusCode(HttpStatus httpStatus) {
			getSource().setStatusCode(httpStatus);
		}

		@Override
		default int getStatus() {
			return getSource().getStatus();
		}
	}

	void addCookie(HttpCookie cookie);

	void addCookie(String name, String value);

	void sendError(int sc) throws IOException;

	void sendError(int sc, String msg) throws IOException;

	void sendRedirect(String location) throws IOException;

	void setStatusCode(HttpStatus httpStatus);

	void setStatus(int sc);

	int getStatus();
}
