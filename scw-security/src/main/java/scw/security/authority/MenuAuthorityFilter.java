package scw.security.authority;

import scw.util.Accept;

public class MenuAuthorityFilter<T extends Authority> implements Accept<T> {

	public boolean accept(T authority) {
		return authority.isMenu();
	}
}
