package scw.security.authority;

public class MenuAuthorityFilter<T extends Authority> implements
		AuthorityFilter<T> {

	public boolean accept(T authority) {
		return authority.isMenu();
	}
}
