package io.basc.framework.net;

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
		default RequestPattern getRequestPattern() {
			return getSource().getRequestPattern();
		}
	}

	@Override
	default RequestPattern getRequestPattern() {
		return RequestPattern.ANY_REQUEST_PATTERN;
	}
}
