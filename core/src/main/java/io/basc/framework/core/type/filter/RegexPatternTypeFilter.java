package io.basc.framework.core.type.filter;

import io.basc.framework.core.Assert;
import io.basc.framework.core.type.ClassMetadata;

import java.util.regex.Pattern;

/**
 * A simple filter for matching a fully-qualified class name with a regex {@link Pattern}.
 */
public class RegexPatternTypeFilter extends AbstractClassTestingTypeFilter {

	private final Pattern pattern;


	public RegexPatternTypeFilter(Pattern pattern) {
		Assert.notNull(pattern, "Pattern must not be null");
		this.pattern = pattern;
	}


	@Override
	protected boolean match(ClassMetadata metadata) {
		return this.pattern.matcher(metadata.getClassName()).matches();
	}

}
