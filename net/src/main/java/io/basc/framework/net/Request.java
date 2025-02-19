package io.basc.framework.net;

import java.net.URI;

/**
 * 一个请求的定义
 * 
 * @author shuchaowen
 *
 */
public interface Request extends Message, RequestPatternCapable {
	public static interface RequestWrapper<W extends Request>
			extends Request, MessageWrapper<W>, RequestPatternCapableWrapper<W> {
		@Override
		default URI getURI() {
			return getSource().getURI();
		}

		@Override
		default PathPattern getRequestPattern() {
			return getSource().getRequestPattern();
		}
	}

	URI getURI();

	@Override
	default PathPattern getRequestPattern() {
		PathPattern pathPattern = new PathPattern();
		pathPattern.setPath(getURI().getPath());
		return pathPattern;
	}
}
