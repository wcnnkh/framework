package scw.security.authority;

public interface AuthorityFilter<T extends Authority> {
	boolean accept(T authority);
}
