package scw.security.authority;

import java.util.List;

public interface TreeAuthority {
	Authority getAuthority();

	List<TreeAuthority> getSubList();
}
