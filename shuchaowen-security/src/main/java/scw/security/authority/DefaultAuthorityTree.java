package scw.security.authority;

import java.io.Serializable;
import java.util.List;

public class DefaultAuthorityTree<T extends Authority> implements AuthorityTree<T>, Serializable {
	private static final long serialVersionUID = 1L;
	private T authority;
	private List<AuthorityTree<T>> subList;

	public T getAuthority() {
		return authority;
	}

	public void setAuthority(T authority) {
		this.authority = authority;
	}

	public List<AuthorityTree<T>> getSubList() {
		return subList;
	}

	public void setSubList(List<AuthorityTree<T>> subList) {
		this.subList = subList;
	}
}
