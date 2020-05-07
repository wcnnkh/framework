package scw.security.authority;

import java.util.Set;

public class AuthorityIdFilter<T extends Authority> implements
		AuthorityFilter<T> {
	private Set<String> ids;

	public AuthorityIdFilter(Set<String> ids) {
		this.ids = ids;
	}

	public boolean accept(T authority) {
		if (ids == null) {
			return false;
		}
		return ids.contains(authority.getId());
	}

}
