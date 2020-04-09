package scw.mvc.action.authority;

import java.util.List;

import scw.mvc.action.Action;


public interface AuthorityManager<A extends Authority> {
	/**
	 * 添加要管理的action
	 * @param action
	 */
	void addAction(Action action);
	
	A getAuthority(String id);
	
	List<A> getAuthoritySubList(String authorityId);
}
