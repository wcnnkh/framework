package run.soeasy.framework.core.match;

import run.soeasy.framework.core.ObjectUtils;

public class IdentityMatcher<T> implements Matcher<T> {
	private static final IdentityMatcher<?> INSTANCE = new IdentityMatcher<>();

	@SuppressWarnings("unchecked")
	public static <E> IdentityMatcher<E> getInstance() {
		return (IdentityMatcher<E>) INSTANCE;
	}

	@Override
	public boolean isPattern(T source) {
		return false;
	}

	@Override
	public boolean match(T pattern, T source) {
		return ObjectUtils.equals(pattern, source);
	}

}
