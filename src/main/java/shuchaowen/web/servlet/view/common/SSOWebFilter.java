package shuchaowen.web.servlet.view.common;

import javax.servlet.http.Cookie;

import shuchaowen.auth.login.Session;
import shuchaowen.auth.login.SessionFactory;
import shuchaowen.web.servlet.Request;
import shuchaowen.web.servlet.Response;
import shuchaowen.web.servlet.action.Filter;
import shuchaowen.web.servlet.action.FilterChain;
import shuchaowen.web.servlet.view.common.enums.Code;

public class SSOWebFilter implements Filter{
	private final SessionFactory sessionFactory;
	private final String sidKey;
	private final String uidKey;
	private final boolean cookie;
	
	public SSOWebFilter(SessionFactory sessionFactory, String uidKey, String sidKey, boolean cookie){
		this.sessionFactory = sessionFactory;
		this.uidKey = uidKey;
		this.sidKey = sidKey;
		this.cookie = cookie;
	}

	public void doFilter(Request request, Response response,
			FilterChain filterChain) throws Throwable {
		if(!checkLogin(request, response)){
			response.write(new Result().setCode(Code.login_status_expired));
			return ;
		}
		filterChain.doFilter(request, response);
	}
	
	protected String getValue(Request request, String key){
		String value = request.getString(key);
		if(value == null && cookie){
			Cookie cookie = request.getCookie(key, false);
			if(cookie != null){
				value = cookie.getValue();
			}
		}
		return value;
	}

	protected final boolean checkLogin(Request request, Response response){
		String sid = getValue(request, sidKey);
		if(sid == null || sid.length() == 0){
			return false;
		}
		
		Session session = sessionFactory.getSession(sid);
		if(session == null){
			return false;
		}

		if(uidKey == null || uidKey.length() == 0){
			return true;
		}
		
		String uid = getValue(request, uidKey);
		if(uid == null || uid.length() == 0){
			return false;
		}
		
		return uid.equals(session.getUid());
	}
}
