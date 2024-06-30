package io.basc.framework.net.pattern;

import java.util.Map;

import io.basc.framework.execution.param.Parameters;
import io.basc.framework.lang.Nullable;
import io.basc.framework.net.Request;
import io.basc.framework.transform.map.MapProperties;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.match.PathMatcher;

public interface PathPattern extends RequestPattern {
	@Nullable
	String getPath();

	PathMatcher getPathMatcher();

	default boolean isPattern() {
		String path = getPath();
		if (path == null) {
			return true;
		}
		return getPathMatcher().isPattern(path);
	}

	@Override
	default Parameters apply(Request request) {
		String path = getPath();
		if (StringUtils.isEmpty(path)) {
			return Parameters.empty();
		}

		String requestPath = request.getURI().getPath();
		Map<String, String> templateVariables = getPathMatcher().extractUriTemplateVariables(path, requestPath);
		if (CollectionUtils.isEmpty(templateVariables)) {
			return Parameters.empty();
		}

		MapProperties mapProperties = new MapProperties(templateVariables);
		return mapProperties.toParameters();
	}

	@Override
	default boolean test(Request request) {
		if (RequestPattern.super.test(request)) {
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
