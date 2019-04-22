package scw.servlet.support;

import scw.beans.annotation.Bean;
import scw.common.utils.StringUtils;
import scw.servlet.Filter;
import scw.servlet.FilterChain;
import scw.servlet.Request;
import scw.servlet.Response;

/**
 * 跨域
 * @author shuchaowen
 *
 */
@Bean(proxy = false)
public class CrossDomainFilter implements Filter {
	public static final String ORIGIN_HEADER = "Access-Control-Allow-Origin";
	public static final String METHODS_HEADER = "Access-Control-Allow-Methods";
	public static final String MAX_AGE_HEADER = "Access-Control-Max-Age";
	public static final String HEADERS_HEADER = "Access-Control-Allow-Headers";
	public static final String CREDENTIALS_HEADER = "Access-Control-Allow-Credentials";

	private final String origin;
	private final String methods;
	private final int maxAge;
	private final String headers;
	private final boolean credentials;

	public CrossDomainFilter() {
		this("*", "*", -1, "*", false);
	}

	public CrossDomainFilter(String origin, String methods, int maxAge, String headers, boolean credentials) {
		this.origin = origin;
		this.methods = methods;
		this.maxAge = maxAge;
		this.headers = headers;
		this.credentials = credentials;
	}

	public void doFilter(Request request, Response response, FilterChain filterChain) throws Throwable {
		String origin = getOrigin();
		/* 允许跨域的主机地址 */
		response.setHeader(ORIGIN_HEADER, StringUtils.isEmpty(origin) ? "*" : origin);

		String methods = getMethods();
		/* 允许跨域的请求方法GET, POST, HEAD 等 */
		response.setHeader(METHODS_HEADER, StringUtils.isEmpty(methods) ? "*" : methods);
		/* 重新预检验跨域的缓存时间 (s) */
		int maxAge = getMaxAge();
		if (maxAge > 0) {
			response.setHeader(MAX_AGE_HEADER, maxAge + "");
		}

		String header = getHeaders();
		/* 允许跨域的请求头 */
		response.setHeader(HEADERS_HEADER, StringUtils.isEmpty(header) ? "*" : header);

		/* 是否携带cookie */
		response.setHeader(CREDENTIALS_HEADER, isCredentials() + "");

		filterChain.doFilter(request, response);
	}

	public String getOrigin() {
		return origin;
	}

	public String getMethods() {
		return methods;
	}

	public int getMaxAge() {
		return maxAge;
	}

	public String getHeaders() {
		return headers;
	}

	public boolean isCredentials() {
		return credentials;
	}
}
