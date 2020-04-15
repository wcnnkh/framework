package scw.security.authority;

import java.util.Collection;
import java.util.List;

public interface AuthorityManager<T extends Authority> {
	T getAuthority(String id);

	Collection<T> getAuthoritys();

	List<T> getAuthoritySubList(String id);

	List<AuthorityTree<T>> getAuthorityTreeList(String id);

	void register(T authority);
}