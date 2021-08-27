package io.basc.framework.security.authority;

import java.io.Serializable;
import java.util.List;

public class AuthorityTree<T extends Authority> implements Serializable {
	private static final long serialVersionUID = 1L;
	private final T authority;
	private final List<AuthorityTree<T>> subList;

	public AuthorityTree(T authority, List<AuthorityTree<T>> subList) {
		this.authority = authority;
		this.subList = subList;
	}

	public T getAuthority() {
		return authority;
	}

	public List<AuthorityTree<T>> getSubList() {
		return subList;
	}
}
