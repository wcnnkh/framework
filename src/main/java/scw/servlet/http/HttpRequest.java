package scw.servlet.http;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import scw.servlet.Request;

public interface HttpRequest extends HttpServletRequest, Request {

	/**
	 * 从cookie中获取数据
	 * 
	 * @param name
	 *            cookie中的名字
	 * @param ignoreCase
	 *            查找时是否忽略大小写
	 * @return
	 */
	Cookie getCookie(String name, boolean ignoreCase);
	
	String getIP();
	
	boolean isAJAX();
}
