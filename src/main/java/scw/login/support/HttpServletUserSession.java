package scw.login.support;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import scw.core.utils.StringUtils;
import scw.login.UserSession;
import scw.login.UserSessionMetaData;

public class HttpServletUserSession extends HttpServletSession implements
		UserSession {
	protected static final String UID_ATTRIBUTE_NAME = "_scw_uid";
	private String uidAttrName;

	public HttpServletUserSession(HttpServletRequest request) {
		this(request, null);
	}

	public HttpServletUserSession(HttpServletRequest request, String uidAttrName) {
		super(request);
		this.uidAttrName = uidAttrName;
	}

	public String getUidAttributeName() {
		return StringUtils.isEmpty(uidAttrName) ? UID_ATTRIBUTE_NAME
				: uidAttrName;
	}

	public String getUid() {
		HttpSession httpSession = getSession(false);
		return (String) (httpSession == null ? null : httpSession
				.getAttribute(getUidAttributeName()));
	}

	public UserSessionMetaData getMetaData() {
		HttpSession httpSession = getSession(false);
		if (httpSession == null) {
			return null;
		}

		String uid = (String) httpSession.getAttribute(getUidAttributeName());
		if (uid == null) {
			return null;
		}

		return new UserSessionMetaData(httpSession.getId(), uid);
	}

	public void login(Object uid) {
		HttpSession httpSession = getSession(true);
		httpSession.setAttribute(getUidAttributeName(), uid);
	}
}
