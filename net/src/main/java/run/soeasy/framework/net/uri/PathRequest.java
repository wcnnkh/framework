package run.soeasy.framework.net.uri;

import run.soeasy.framework.net.Request;

public interface PathRequest extends Request {
	public static interface UriRequestWrapper<W extends PathRequest> extends PathRequest, RequestWrapper<W> {
		@Override
		default String getPath() {
			return getSource().getPath();
		}
	}

	String getPath();
}
