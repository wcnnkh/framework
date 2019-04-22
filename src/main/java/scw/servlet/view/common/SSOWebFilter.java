package scw.servlet.view.common;

import javax.servlet.http.Cookie;

import scw.servlet.Filter;
import scw.servlet.FilterChain;
import scw.servlet.Request;
import scw.servlet.Response;
import scw.servlet.view.common.enums.ResultCode;
import scw.utils.login.Session;
import scw.utils.login.LoginFactory;

public class SSOWebFilter implements Filter{
	private final LoginFactory loginFactory;
	private final String sidKey;
	private final String uidKey;
	private final boolean cookie;
	
	public SSOWebFilter(LoginFactory loginFactory, String uidKey, String sidKey, boolean cookie){
		this.loginFactory = loginFactory;
		this.uidKey = uidKey;
		this.sidKey = sidKey;
		this.cookie = cookie;
	}

	public void doFilter(Request request, Response response,
			FilterChain filterChain) throws Throwable {
		if(!checkLogin(request, response)){
			response.write(new Result().setCode(ResultCode.login_status_expired));
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
		
		Session session = loginFactory.getSession(sid);
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
