package io.basc.framework.net.uri;

import java.util.Map;

import io.basc.framework.core.convert.transform.stereotype.Properties;
import io.basc.framework.net.Request;
import io.basc.framework.net.RequestPattern;
import io.basc.framework.net.WildcardRequestPattern;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.collections.CollectionUtils;
import io.basc.framework.util.match.AntPathMatcher;
import io.basc.framework.util.match.PathMatcher;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

@Data
@EqualsAndHashCode(of = "path", callSuper = true)
@ToString(of = "path", callSuper = true)
public class PathPattern extends WildcardRequestPattern {
	private String path;
	@NonNull
	private PathMatcher pathMatcher = AntPathMatcher.DEFAULT;

	public boolean isPattern() {
		String path = getPath();
		if (path == null) {
			return true;
		}
		return getPathMatcher().isPattern(path);
	}

	@Override
	public Properties apply(Request request) {
		String path = getPath();
		if (StringUtils.isEmpty(path)) {
			return Properties.EMPTY_PROPERTIES;
		}

		RequestPattern requestPattern = request.getRequestPattern();
		if (requestPattern instanceof PathPattern) {
			PathPattern requestPathPattern = (PathPattern) requestPattern;
			Map<String, String> templateVariables = getPathMatcher().extractUriTemplateVariables(path,
					requestPathPattern.getPath());
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
