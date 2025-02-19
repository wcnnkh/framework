package io.basc.framework.web.pattern;

import io.basc.framework.http.server.ServerHttpRequest;
import io.basc.framework.util.StringUtils;
import io.basc.framework.web.WebException;

public class PathRegistry extends WebProcessorRegistry<String, WebException> {

	public void add(String pattern, String location) {
		add(pattern, new AbsolutePathProcessor(location));
	}

	@Override
	public String process(ServerHttpRequest request) throws WebException {
		String path = super.process(request);
		if (path != null) {
			path = StringUtils.cleanPath(path);
		}
		return path;
	}
}
