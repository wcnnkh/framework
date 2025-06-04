package run.soeasy.framework.core.match;

import run.soeasy.framework.core.ObjectUtils;

class IdentityMatcher implements StringMatcher {
	static final IdentityMatcher INSTANCE = new IdentityMatcher();

	@Override
	public boolean isPattern(String source) {
		return false;
	}

	@Override
	public boolean match(String pattern, String source) {
		return ObjectUtils.equals(pattern, source);
	}

	@Override
	public String extractWithinPattern(String pattern, String text) {
		return text;
	}
}
