package io.basc.framework.web;

import io.basc.framework.core.utils.ObjectUtils;
import io.basc.framework.http.HttpCookie;
import io.basc.framework.http.HttpHeaders;
import io.basc.framework.http.HttpStatus;
import io.basc.framework.http.MediaType;
import io.basc.framework.net.message.OutputMessageWrapper;
import io.basc.framework.util.XUtils;

import java.io.IOException;
import java.io.PrintWriter;

public class ServerHttpResponseWrapper extends OutputMessageWrapper<ServerHttpResponse> implements ServerHttpResponse {

	public ServerHttpResponseWrapper(ServerHttpResponse targetResponse) {
		super(targetResponse);
	}

	public <T> T getDelegate(Class<T> targetType) {
		return XUtils.getDelegate(wrappedTarget, targetType);
	}

	public void flush() throws IOException {
		wrappedTarget.flush();
	}

	public boolean isCommitted() {
		return wrappedTarget.isCommitted();
	}

	public PrintWriter getWriter() throws IOException {
		return wrappedTarget.getWriter();
	}

	public void close() throws IOException {
		wrappedTarget.close();
	}

	public MediaType getContentType() {
		return wrappedTarget.getContentType();
	}

	public void addCookie(HttpCookie cookie) {
		wrappedTarget.addCookie(cookie);
	}

	public void addCookie(String name, String value) {
		wrappedTarget.addCookie(name, value);
	}

	public void sendError(int sc) throws IOException {
		wrappedTarget.sendError(sc);
	}

	public void sendRedirect(String location) throws IOException {
		wrappedTarget.sendRedirect(location);
	}

	public void sendError(int sc, String msg) throws IOException {
		wrappedTarget.sendError(sc, msg);
	}

	public void setStatusCode(HttpStatus httpStatus) {
		wrappedTarget.setStatusCode(httpStatus);
	}

	public void setStatus(int sc) {
		wrappedTarget.setStatus(sc);
	}

	public int getStatus() {
		return wrappedTarget.getStatus();
	}

	public HttpHeaders getHeaders() {
		return wrappedTarget.getHeaders();
	}

	public void setContentType(MediaType contentType) {
		wrappedTarget.setContentType(contentType);
	}

	@Override
	public String toString() {
		return wrappedTarget.toString();
	}

	@Override
	public int hashCode() {
		return wrappedTarget.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj instanceof ServerHttpResponseWrapper) {
			return ObjectUtils.nullSafeEquals(wrappedTarget, ((ServerHttpResponseWrapper) obj).wrappedTarget);
		}
		return false;
	}
}
