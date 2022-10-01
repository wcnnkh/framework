package io.basc.framework.security.authority;

import java.util.function.Predicate;

public class MenuAuthorityFilter<T extends Authority> implements Predicate<T> {

	public boolean test(T authority) {
		return authority.isMenu();
	}
}
