package scw.security.authority.http;

import java.util.List;

public interface TreeHttpAuthority {
	HttpAuthority getHttpAuthority();

	List<TreeHttpAuthority> getSubList();
}
