package io.basc.framework.net.uri;

import io.basc.framework.net.Request;

public interface PathRequest extends Request {
	public static interface UriRequestWrapper<W extends PathRequest> extends PathRequest, RequestWrapper<W> {
		@Override
		default String getPath() {
			return getSource().getPath();
		}

		@Override
		default PathPattern getRequestPattern() {
			return getSource().getRequestPattern();
		}
	}

	String getPath();

	@Override
	default PathPattern getRequestPattern() {
		PathPattern pathPattern = new PathPattern();
		pathPattern.setPath(getPath());
		return pathPattern;
	}
}
