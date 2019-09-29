package scw.security.authority;

import java.util.List;

public interface AuthorityManager<T extends Authority> {
	T getAuthority(String id);

	List<T> getAuthorityList();

	List<T> getAuthoritySubList(String id);

	AuthorityTree<T> getAuthorityTree(String id);
}