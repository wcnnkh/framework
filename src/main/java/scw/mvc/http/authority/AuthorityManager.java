package scw.mvc.http.authority;

import java.util.List;

import scw.beans.annotation.AutoImpl;
import scw.mvc.http.HttpParameterRequest;

@AutoImpl(DefaultAuthorityManager.class)
public interface AuthorityManager {
	Authority getHttpAuthority(HttpParameterRequest httpParameterRequest);

	List<Authority> getList();

	List<Authority> getList(long id);

	List<Long> getSubList(long id);

	Authority getHttpAuthority(long id);

	TreeHttpAuthority getTreeHttpAuthority();

	TreeHttpAuthority getTreeHttpAuthority(long id);
}
