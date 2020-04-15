package scw.mvc.action.authority;

import scw.mvc.action.Action;
import scw.security.authority.AuthorityManager;

public interface ActionAuthorityManager<T extends ActionAuthority, A extends Action>
		extends AuthorityManager<T> {
	void register(A action);

	T getAuthority(A action);
}
