package io.basc.framework.net.uri;

import java.util.Map;
import java.util.Objects;

import io.basc.framework.core.convert.transform.stereotype.Properties;
import io.basc.framework.net.Request;
import io.basc.framework.net.WildcardRequestPattern;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.collections.CollectionUtils;
import io.basc.framework.util.match.AntPathMatcher;
import io.basc.framework.util.match.PathMatcher;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class PathPattern extends WildcardRequestPattern {
	@NonNull
	private UriComponentsBuilder builder = UriComponentsBuilder.newInstance();
	@NonNull
	private PathMatcher pathMatcher = AntPathMatcher.DEFAULT;

	public String getPath() {
		return builder.build().getPath();
	}

	public void setPath(String path) {
		builder.path(path);
	}

	public boolean isPattern() {
		String path = getPath();
		if (path == null) {
			return true;
		}
		return getPathMatcher().isPattern(path);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(getPath());
	}

	@Override
	public boolean equals(Object other) {
		if (other == null) {
			return false;
		}

		if (other instanceof PathPattern) {
			String path = getPath();
			String otherPath = ((PathPattern) other).getPath();
			return StringUtils.equals(path, otherPath);
		}
		return false;
	}

	@Override
	public String toString() {
		return builder.toUriString();
	}

	@Override
	public Properties apply(Request request) {
		String path = getPath();
		if (StringUtils.isEmpty(path)) {
			return Properties.EMPTY_PROPERTIES;
		}

		if (request instanceof PathRequest) {
			String requestPath = ((PathRequest) request).getPath();
			Map<String, String> templateVariables = getPathMatcher().extractUriTemplateVariables(path, requestPath);
			if (CollectionUtils.isEmpty(templateVariables)) {
				return Properties.EMPTY_PROPERTIES;
			}
			return Properties.forMap(templateVariables);
		}
		return Properties.EMPTY_PROPERTIES;
	}

	@Override
	public boolean test(Request request) {
		if (super.test(request)) {
			String path = getPath();
			if (path == null) {
				return true;
			}

			if (request instanceof PathRequest) {
				String requestPath = ((PathRequest) request).getPath();
				if (isPattern()) {
					if (getPathMatcher().match(path, requestPath)) {
						return true;
					}
					return false;
				} else {
					if (!StringUtils.equals(path, requestPath)) {
						return false;
					}
				}
			}
		}
		return false;
	}
}
