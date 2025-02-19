package io.basc.framework.net;

import io.basc.framework.util.function.Wrapper;

public interface RequestPatternCapable {
	public static interface RequestPatternCapableWrapper<W extends RequestPatternCapable>
			extends RequestPatternCapable, Wrapper<W> {
		@Override
		default RequestPattern getRequestPattern() {
			return getSource().getRequestPattern();
		}
	}

	RequestPattern getRequestPattern();
}
