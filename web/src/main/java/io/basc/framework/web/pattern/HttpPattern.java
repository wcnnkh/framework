package io.basc.framework.web.pattern;

import java.util.Collections;
import java.util.Map;

import io.basc.framework.lang.Nullable;
import io.basc.framework.net.MimeTypes;
import io.basc.framework.util.AntPathMatcher;
import io.basc.framework.util.ObjectUtils;
import io.basc.framework.util.PathMatcher;
import io.basc.framework.util.StringUtils;
import io.basc.framework.web.ServerHttpRequest;
import io.basc.framework.web.WebUtils;

public class HttpPattern implements ServerHttpRequestAccept, Cloneable, Comparable<HttpPattern> {
	private static final PathMatcher DEFAULT_PATH_MATCHER = new AntPathMatcher();
	private PathMatcher pathMatcher;
	private final String path;
	private String method;
	private MimeTypes consumes;
	private MimeTypes produces;

	public HttpPattern(String path) {
		this(path, null, null);
	}

	public HttpPattern(String path, @Nullable String method) {
		this(path, method, null);
	}

	public HttpPattern(String path, @Nullable String method, @Nullable MimeTypes consumes) {
		this(path, method, consumes, null);
	}

	public HttpPattern(String path, @Nullable String method, @Nullable MimeTypes consumes,
			@Nullable MimeTypes produces) {
		this.path = path;
		this.method = method;
		this.consumes = consumes == null ? null : consumes.readyOnly();
		this.produces = produces == null ? null : produces.readyOnly();
	}

	protected HttpPattern(HttpPattern httpPattern) {
		this.path = httpPattern == null ? null : httpPattern.path;
		this.method = httpPattern == null ? null : httpPattern.method;
		this.consumes = httpPattern == null ? null : httpPattern.consumes;
		this.produces = httpPattern == null ? null : httpPattern.produces;
		if (httpPattern != null) {
			this.pathMatcher = httpPattern.pathMatcher;
		}
	}

	/**
	 * 返回一个新的HttpPattern
	 * 
	 * @param method
	 * @return
	 */
	public HttpPattern setMethod(String method) {
		HttpPattern httpPattern = new HttpPattern(this);
		httpPattern.method = method;
		return httpPattern;
	}

	/**
	 * 返回一个新的HttpPattern
	 * 
	 * @param consumes
	 * @return
	 */
	public HttpPattern setConsumes(MimeTypes consumes) {
		HttpPattern httpPattern = new HttpPattern(this);
		httpPattern.consumes = consumes;
		return httpPattern;
	}

	/**
	 * 返回一个新的HttpPattern
	 * 
	 * @param produces
	 * @return
	 */
	public HttpPattern setProduces(MimeTypes produces) {
		HttpPattern httpPattern = new HttpPattern(this);
		httpPattern.produces = produces;
		return httpPattern;
	}

	public PathMatcher getPathMatcher() {
		return pathMatcher == null ? DEFAULT_PATH_MATCHER : pathMatcher;
	}

	public String getPath() {
		return path;
	}

	@Nullable
	public String getMethod() {
		return method;
	}

	public MimeTypes getConsumes() {
		return consumes == null ? MimeTypes.EMPTY : consumes;
	}

	public boolean hasConsumes() {
		return consumes != null && !consumes.isEmpty();
	}

	/**
	 * 不参与以下方法
	 * 
	 * @see #accept(ServerHttpRequest)
	 * @see #compareTo(HttpPattern)
	 * @see #equals(Object)
	 * @see #hashCode()
	 * @return
	 */
	public MimeTypes getProduces() {
		return produces == null ? MimeTypes.EMPTY : produces;
	}

	public boolean hasProduces() {
		return produces != null && !produces.isEmpty();
	}

	public boolean isPattern() {
		if (path == null || method == null) {
			return true;
		}

		return getPathMatcher().isPattern(path);
	}

	@Override
	public boolean accept(ServerHttpRequest request) {
		if (method != null && !ObjectUtils.nullSafeEquals(method, request.getRawMethod())) {
			return false;
		}

		if (consumes != null && !consumes.isCompatibleWith(request.getContentType())) {
			return false;
		}

		if (path != null) {
			if (isPattern()) {
				if (getPathMatcher().match(path, request.getPath())) {
					WebUtils.setHttpPattern(request, this);
					WebUtils.setRestfulParameterMap(request, extractUriTemplateVariables(request.getPath()));
					return true;
				}
				return false;
			} else {
				if (!StringUtils.equals(path, request.getPath())) {
					return false;
				}
			}
		}
		WebUtils.setHttpPattern(request, this);
		return true;
	}

	public Map<String, String> extractUriTemplateVariables(String path) {
		if (path == null) {
			return Collections.emptyMap();
		}

		return getPathMatcher().extractUriTemplateVariables(this.path, path);
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

		if (consumes != null) {
			code += consumes.hashCode();
		}
		return code;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj instanceof HttpPattern) {
			HttpPattern httpPattern = (HttpPattern) obj;
			if (!ObjectUtils.nullSafeEquals(method, httpPattern.method)) {
				return false;
			}

			if (!ObjectUtils.nullSafeEquals(consumes, httpPattern.consumes)) {
				return false;
			}

			if (StringUtils.isNotEmpty(path) && StringUtils.isNotEmpty(((HttpPattern) obj).path)) {
				if (getPathMatcher().match(path, ((HttpPattern) obj).path)
						&& getPathMatcher().match(((HttpPattern) obj).path, path)) {
					return true;
				}

				if (((HttpPattern) obj).getPathMatcher().match(path, ((HttpPattern) obj).path)
						&& ((HttpPattern) obj).getPathMatcher().match(((HttpPattern) obj).path, path)) {
					return true;
				}
			}
			return ObjectUtils.nullSafeEquals(path, ((HttpPattern) obj).path);
		}
		return false;
	}

	@Override
	public String toString() {
		if (method == null && path == null && consumes == null) {
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

		if (consumes != null) {
			if (sb.length() > 0) {
				sb.append(" ");
			}
			sb.append(consumes);
		}
		return sb.toString();
	}

	@Override
	public HttpPattern clone() {
		return new HttpPattern(this);
	}

	@Override
	public int compareTo(HttpPattern o) {
		if (this.equals(o)) {
			return 0;
		}

		if (path != null && o.path != null) {
			if (getPathMatcher().match(o.path, path) || o.getPathMatcher().match(o.path, path)) {
				return -1;
			}

			if (getPathMatcher().match(path, o.path) || o.getPathMatcher().match(path, o.path)) {
				return 1;
			}
		}

		if (o.consumes != null && this.consumes != null) {
			return this.consumes.compareTo(o.consumes);
		}

		if (path == null && o.path == null) {
			return 0;
		}

		if (path == null) {
			return -1;
		}

		if (o.path == null) {
			return 1;
		}
		return path.compareTo(o.path);
	}

	/**
	 * 返回一个新的HttpPattern
	 * 
	 * @param pathMatcher
	 * @return
	 */
	public HttpPattern setPathMatcher(PathMatcher pathMatcher) {
		HttpPattern httpPattern = new HttpPattern(this);
		httpPattern.pathMatcher = pathMatcher;
		return httpPattern;
	}
}
