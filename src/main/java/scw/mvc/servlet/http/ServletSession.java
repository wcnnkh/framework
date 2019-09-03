package scw.mvc.servlet.http;

import java.util.Enumeration;

import javax.servlet.http.HttpSession;

import scw.login.Session;

public class ServletSession implements Session{
	private HttpSession httpSession;
	
	public ServletSession(HttpSession httpSession){
		this.httpSession = httpSession;
	}
	
	public long getCreationTime() {
		return httpSession.getCreationTime();
	}

	public String getId() {
		return httpSession.getId();
	}

	public long getLastAccessedTime() {
		return httpSession.getLastAccessedTime();
	}

	public void setMaxInactiveInterval(int interval) {
		httpSession.setMaxInactiveInterval(interval);
	}

	public int getMaxInactiveInterval() {
		return httpSession.getMaxInactiveInterval();
	}

	public Object getAttribute(String name) {
		return httpSession.getAttribute(name);
	}

	public Enumeration<String> getAttributeNames() {
		return httpSession.getAttributeNames();
	}

	public void setAttribute(String name, Object value) {
		httpSession.setAttribute(name, value);
	}

	public void removeAttribute(String name) {
		httpSession.removeAttribute(name);
	}

	public void invalidate() {
		httpSession.invalidate();
	}

	public boolean isNew() {
		return httpSession.isNew();
	}

	public HttpSession getHttpSession() {
		return httpSession;
	}
}
