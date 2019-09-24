package scw.mvc.http.authority;

import java.util.List;

public interface TreeHttpAuthority {
	Authority getHttpAuthority();

	List<TreeHttpAuthority> getSubList();
}
