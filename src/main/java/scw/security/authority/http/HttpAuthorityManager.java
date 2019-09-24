package scw.security.authority.http;

import java.util.List;

import scw.beans.annotation.AutoImpl;
import scw.security.authority.AuthorityManager;

@AutoImpl(DefaultHttpAuthorityManager.class)
public interface HttpAuthorityManager extends AuthorityManager {
	HttpAuthority getHttpAuthority(String requestPath, String method);

	List<HttpAuthority> getList();

	List<HttpAuthority> getList(long id);

	List<Long> getSubList(long id);

	HttpAuthority getHttpAuthority(long id);

	TreeHttpAuthority getTreeHttpAuthority();

	TreeHttpAuthority getTreeHttpAuthority(long id);
}
