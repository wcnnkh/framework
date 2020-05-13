package scw.mvc.http;

import java.io.IOException;

import scw.mvc.ServerResponseWrapper;
import scw.net.http.HttpCookie;
import scw.net.http.HttpHeaders;
import scw.net.http.HttpStatus;

public class ServerHttpResponseWrapper extends ServerResponseWrapper<ServerHttpResponse> implements ServerHttpResponse{

	public ServerHttpResponseWrapper(ServerHttpResponse targetResponse) {
		super(targetResponse);
	}

	public void addCookie(HttpCookie cookie) {
		targetResponse.addCookie(cookie);
	}

	public void addCookie(String name, String value) {
		targetResponse.addCookie(name, value);
	}

	public void sendError(int sc) throws IOException {
		targetResponse.sendError(sc);
	}

	public void sendRedirect(String location) throws IOException {
		targetResponse.sendRedirect(location);
	}
	
	public void sendError(int sc, String msg) throws IOException {
		targetResponse.sendError(sc, msg);
	}

	public void setStatusCode(HttpStatus httpStatus) {
		targetResponse.setStatusCode(httpStatus);
	}
	
	public void setStatus(int sc) {
		targetResponse.setStatus(sc);
	}

	public int getStatus() {
		return targetResponse.getStatus();
	}

	public HttpHeaders getHeaders() {
		return targetResponse.getHeaders();
	}

}
