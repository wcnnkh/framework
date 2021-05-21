package scw.http.server.pattern;

import java.util.Collections;
import java.util.Map;

import scw.core.utils.ObjectUtils;
import scw.core.utils.StringUtils;
import scw.http.HttpMethod;
import scw.http.server.ServerHttpRequest;
import scw.lang.Nullable;
import scw.util.AntPathMatcher;
import scw.util.PathMatcher;
import scw.web.WebUtils;

public class HttpPattern implements ServerHttpRequestAccept, Cloneable {
	private static final PathMatcher DEFAULT_PATH_MATCHER = new AntPathMatcher();

	private final String path;
	private final HttpMethod method;
	private PathMatcher pathMatcher;

	public HttpPattern(String pattern) {
		this(pattern, null);
	}

	public HttpPattern(String path, @Nullable HttpMethod method) {
		this.path = path;
		this.method = method;
	}

	protected HttpPattern(HttpPattern httpPattern) {
		this.path = httpPattern == null ? null : httpPattern.path;
		this.method = httpPattern == null ? null : httpPattern.method;
		if (httpPattern != null) {
			this.pathMatcher = httpPattern.pathMatcher;
		}
	}

	public PathMatcher getPathMatcher() {
		return pathMatcher == null ? DEFAULT_PATH_MATCHER : pathMatcher;
	}

	public void setPathMatcher(PathMatcher pathMatcher) {
		this.pathMatcher = pathMatcher;
	}

	public String getPath() {
		return path;
	}

	public HttpMethod getMethod() {
		return method;
	}

	public boolean isPattern() {
		if (path == null || method == null) {
			return true;
		}

		return getPathMatcher().isPattern(path);
	}

	@Override
	public boolean accept(ServerHttpRequest request) {
		if (method != null && !ObjectUtils.nullSafeEquals(method, request.getMethod())) {
			return false;
		}

		if (path != null) {
			if (isPattern()) {
				if(getPathMatcher().match(path, request.getPath())) {
					WebUtils.setRestfulParameterMap(request,
							getPathMatcher().extractUriTemplateVariables(path, request.getPath()));
					return true;
				}
				return false;
			} else {
				if (!StringUtils.equals(path, request.getPath())) {
					return false;
				}
			}
		}
		return true;
	}

	public Map<String, String> extractUriTemplateVariables(String path) {
		if (path == null) {
			return Collections.emptyMap();
		}

		return getPathMatcher().extractUriTemplateVariables(path, path);
	}

	@Override
	public int hashCode() {
		int code = 0;
		if (path != null) {
			code += path.hashCode();
		}

		if (method != null) {
			code += method.hashCode();
		}
		return code;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj instanceof HttpPattern) {
			if (!ObjectUtils.nullSafeEquals(method, ((HttpPattern) obj).method)) {
				return false;
			}

			if (StringUtils.isEmpty(path) && StringUtils.isEmpty(((HttpPattern) obj).path)) {
				return true;
			}

			if (StringUtils.isNotEmpty(path) && StringUtils.isNotEmpty(((HttpPattern) obj).path)) {
				return getPathMatcher().match(path, ((HttpPattern) obj).path)
						|| getPathMatcher().match(((HttpPattern) obj).path, path);
			}
		}
		return false;
	}

	@Override
	public String toString() {
		if (method == null && path == null) {
			return "[ANY]";
		}

		StringBuilder sb = new StringBuilder();
		if (method != null) {
			sb.append(method);
		}

		if (path != null) {
			if (sb.length() > 0) {
				sb.append(" ");
			}
			sb.append(path);
		}
		return sb.toString();
	}

	@Override
	public HttpPattern clone() {
		return new HttpPattern(this);
	}
}
