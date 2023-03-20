package io.basc.framework.core.type.filter;

import java.util.regex.Pattern;

import io.basc.framework.core.type.ClassMetadata;
import io.basc.framework.util.Assert;

/**
 * A simple filter for matching a fully-qualified class name with a regex
 * {@link Pattern}.
 *
 * @author Mark Fisher
 * @author Juergen Hoeller
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
