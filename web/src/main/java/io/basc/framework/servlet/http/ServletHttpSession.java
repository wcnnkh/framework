package io.basc.framework.servlet.http;

import io.basc.framework.http.HttpSession;
import io.basc.framework.util.ObjectUtils;
import io.basc.framework.util.collections.Elements;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ServletHttpSession implements HttpSession {
	@NonNull
	private javax.servlet.http.HttpSession httpSession;

	public Object getAttribute(String name) {
		return httpSession.getAttribute(name);
	}

	@Override
	public Elements<String> getAttributeNames() {
		return Elements.of(() -> httpSession.getAttributeNames());
	}

	public void setAttribute(String name, Object o) {
		httpSession.setAttribute(name, o);
	}

	public void removeAttribute(String name) {
		httpSession.removeAttribute(name);
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

	public void setMaxInactiveInterval(int maxInactiveInterval) {
		httpSession.setMaxInactiveInterval(maxInactiveInterval);
	}

	public int getMaxInactiveInterval() {
		return httpSession.getMaxInactiveInterval();
	}

	public void invalidate() {
		httpSession.invalidate();
	}

	public boolean isNew() {
		return httpSession.isNew();
	}

	@Override
	public int hashCode() {
		return httpSession.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj == this) {
			return true;
		}

		if (obj instanceof ServletHttpSession) {
			return ObjectUtils.equals(((ServletHttpSession) obj).httpSession, httpSession);
		}
		return false;
	}

	@Override
	public String toString() {
		return httpSession.toString();
	}
}
