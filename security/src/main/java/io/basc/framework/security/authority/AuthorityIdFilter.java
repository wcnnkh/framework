package io.basc.framework.security.authority;

import java.util.Set;
import java.util.function.Predicate;

public class AuthorityIdFilter<T extends Authority> implements Predicate<T> {
	private Set<String> ids;

	public AuthorityIdFilter(Set<String> ids) {
		this.ids = ids;
	}

	public boolean test(T authority) {
		if (ids == null) {
			return false;
		}
		return ids.contains(authority.getId());
	}

}
