package io.basc.framework.env1;

import java.util.function.Predicate;

public interface Profiles {
	/**
	 * Test if this {@code Profiles} instance <em>matches</em> against the given
	 * active profiles predicate.
	 * 
	 * @param activeProfiles a predicate that tests whether a given profile is
	 *                       currently active
	 */
	boolean matches(Predicate<String> activeProfiles);
}
