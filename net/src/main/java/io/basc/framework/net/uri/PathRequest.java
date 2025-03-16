package io.basc.framework.net.uri;

import io.basc.framework.net.Request;

public interface PathRequest extends Request {
	public static interface UriRequestWrapper<W extends PathRequest> extends PathRequest, RequestWrapper<W> {
		@Override
		default String getPath() {
			return getSource().getPath();
		}
	}

	String getPath();
}
