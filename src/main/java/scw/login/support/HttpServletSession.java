package scw.login.support;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import scw.login.Session1;

public class HttpServletSession implements Session1 {
	private final HttpServletRequest request;

	public HttpServletSession(HttpServletRequest request) {
		this.request = request;
	}

	public HttpServletRequest getRequest() {
		return request;
	}
	
	public HttpSession getSession(){
		return request.getSession();
	}
	
	public HttpSession getSession(boolean create){
		return request.getSession(create);
	}

	public long getCreationTime() {
		HttpSession httpSession = request.getSession(false);
		return httpSession == null ? -1 : httpSession.getCreationTime();
	}

	public String getId() {
		HttpSession httpSession = request.getSession(false);
		return httpSession == null ? null : httpSession.getId();
	}

	public long getLastAccessedTime() {
		HttpSession httpSession = request.getSession(false);
		return httpSession == null ? -1 : httpSession.getLastAccessedTime();
	}

	public void setMaxInactiveInterval(int interval) {
		HttpSession httpSession = request.getSession(false);
		if (httpSession == null) {
			return;
		}

		httpSession.setMaxInactiveInterval(interval);
	}

	public int getMaxInactiveInterval() {
		HttpSession httpSession = request.getSession(false);
		return httpSession == null ? -1 : httpSession.getMaxInactiveInterval();
	}

	public Object getAttribute(String name) {
		HttpSession httpSession = request.getSession(false);
		return httpSession == null ? null : httpSession.getAttribute(name);
	}

	public Enumeration<String> getAttributeNames() {
		HttpSession httpSession = request.getSession(false);
		return httpSession == null ? null : httpSession.getAttributeNames();
	}

	public void setAttribute(String name, Object value) {
		HttpSession httpSession = request.getSession(false);
		if (httpSession == null) {
			return;
		}

		httpSession.setAttribute(name, value);
	}

	public void removeAttribute(String name) {
		HttpSession httpSession = request.getSession(false);
		if (httpSession == null) {
			return;
		}

		httpSession.removeAttribute(name);
	}

	public void invalidate() {
		HttpSession httpSession = request.getSession(false);
		if (httpSession == null) {
			return;
		}

		httpSession.invalidate();
	}

	public boolean isNew() {
		HttpSession httpSession = request.getSession(false);
		if (httpSession == null) {
			return false;
		}

		return httpSession.isNew();
	}
}
