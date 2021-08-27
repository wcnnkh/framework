package io.basc.framework.security.authority;

import io.basc.framework.util.Accept;

public class MenuAuthorityFilter<T extends Authority> implements Accept<T> {

	public boolean accept(T authority) {
		return authority.isMenu();
	}
}
