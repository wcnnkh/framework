package scw.security.authority;

import java.util.List;

public interface AuthorityTree<T extends Authority> {
	T getAuthority();

	List<AuthorityTree<T>> getSubList();
}
