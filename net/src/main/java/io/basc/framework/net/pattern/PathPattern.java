package io.basc.framework.net.pattern;

import java.util.Map;

import io.basc.framework.core.convert.transform.stereotype.Properties;
import io.basc.framework.net.Request;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.collections.CollectionUtils;
import io.basc.framework.util.match.AntPathMatcher;
import io.basc.framework.util.match.PathMatcher;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(of = "path", callSuper = true)
@ToString(of = "path", callSuper = true)
public class PathPattern extends WildcardRequestPattern {
	private String path;
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

		String requestPath = request.getURI().getPath();
		Map<String, String> templateVariables = getPathMatcher().extractUriTemplateVariables(path, requestPath);
		if (CollectionUtils.isEmpty(templateVariables)) {
			return Properties.EMPTY_PROPERTIES;
		}
		return Properties.forMap(templateVariables);
	}

	@Override
	public boolean test(Request request) {
		if (super.test(request)) {
			String path = getPath();
			if (path != null) {
				String requestPath = request.getURI().getPath();
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
			return true;
		}
		return false;
	}
}
