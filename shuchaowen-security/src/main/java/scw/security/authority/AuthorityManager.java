package scw.security.authority;

import java.util.Collection;
import java.util.List;

import scw.beans.annotation.AopEnable;

@AopEnable(false)
public interface AuthorityManager<T extends Authority> {
	void register(T authority);
	
	T getAuthority(String id);

	List<T> getAuthorityList(AuthorityFilter<T> authorityFilter);
	
	List<T> getRootList(AuthorityFilter<T> authorityFilter);
	
	List<AuthorityTree<T>> getAuthorityTreeList(AuthorityFilter<T> authorityFilter);

	List<T> getAuthoritySubList(String id, AuthorityFilter<T> authorityFilter);
	
	List<T> getParentList(String id, AuthorityFilter<T> authorityFilter);

	List<AuthorityTree<T>> getAuthorityTreeList(String id, AuthorityFilter<T> authorityFilter);

	List<AuthorityTree<T>> getRelationAuthorityTreeList(Collection<String> ids, AuthorityFilter<T> authorityFilter);
	
	List<T> getRelationAuthorityList(Collection<String> ids, AuthorityFilter<T> authorityFilter);
}