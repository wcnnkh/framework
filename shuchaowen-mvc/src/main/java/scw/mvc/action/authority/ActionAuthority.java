package scw.mvc.action.authority;

import scw.security.authority.Authority;

public interface ActionAuthority extends Authority {
	boolean isMenuAction();
}
