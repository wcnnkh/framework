package scw.servlet.http.filter;

import javax.servlet.http.HttpServletResponse;

import scw.core.utils.StringUtils;

public final class CrossDomainDefinition {
	public static final String ORIGIN_HEADER = "Access-Control-Allow-Origin";
	public static final String METHODS_HEADER = "Access-Control-Allow-Methods";
	public static final String MAX_AGE_HEADER = "Access-Control-Max-Age";
	public static final String HEADERS_HEADER = "Access-Control-Allow-Headers";
	public static final String CREDENTIALS_HEADER = "Access-Control-Allow-Credentials";

	private final String origin;
	private final String headers;
	private final String methods;
	private final boolean credentials;
	private final int maxAge;

	public CrossDomainDefinition(String origin, String headers, String methods,
			boolean credentials, int maxAge) {
		this.origin = origin;
		this.headers = headers;
		this.methods = methods;
		this.credentials = credentials;
		this.maxAge = maxAge;
	}

	public void write(HttpServletResponse response) {
		/* 允许跨域的主机地址 */
		response.setHeader(ORIGIN_HEADER, StringUtils.isEmpty(origin) ? "*"
				: origin);

		/* 允许跨域的请求方法GET, POST, HEAD 等 */
		response.setHeader(METHODS_HEADER, StringUtils.isEmpty(methods) ? "*"
				: methods);
		/* 重新预检验跨域的缓存时间 (s) */
		if (maxAge > 0) {
			response.setHeader(MAX_AGE_HEADER, maxAge + "");
		}

		/* 允许跨域的请求头 */
		response.setHeader(HEADERS_HEADER, StringUtils.isEmpty(headers) ? "*"
				: headers);

		/* 是否携带cookie */
		response.setHeader(CREDENTIALS_HEADER, credentials + "");
	}
}
