package io.basc.framework.core.annotation;

import java.util.Arrays;

import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Assert;
import io.basc.framework.util.StringUtils;

/**
 * {@link AnnotationFilter} implementation used for
 * {@link AnnotationFilter#packages(String...)}.
 *
 * @author https://github.com/spring-projects/spring-framework/blob/main/spring-core/src/main/java/org/springframework/core/annotation/PackagesAnnotationFilter.java
 */
final class PackagesAnnotationFilter implements AnnotationFilter {

	private final String[] prefixes;

	private final int hashCode;

	PackagesAnnotationFilter(String... packages) {
		Assert.notNull(packages, "Packages array must not be null");
		this.prefixes = new String[packages.length];
		for (int i = 0; i < packages.length; i++) {
			String pkg = packages[i];
			Assert.hasText(pkg, "Packages array must not have empty elements");
			this.prefixes[i] = pkg + ".";
		}
		Arrays.sort(this.prefixes);
		this.hashCode = Arrays.hashCode(this.prefixes);
	}

	@Override
	public boolean matches(String annotationType) {
		for (String prefix : this.prefixes) {
			if (annotationType.startsWith(prefix)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean equals(@Nullable Object other) {
		if (this == other) {
			return true;
		}
		if (other == null || getClass() != other.getClass()) {
			return false;
		}
		return Arrays.equals(this.prefixes, ((PackagesAnnotationFilter) other).prefixes);
	}

	@Override
	public int hashCode() {
		return this.hashCode;
	}

	@Override
	public String toString() {
		return "Packages annotation filter: " + StringUtils.arrayToCommaDelimitedString(this.prefixes);
	}

}
