package scw.mvc.action.authority;

import scw.security.authority.Authority;

public interface ActionAuthority extends Authority {
	/**
	 * 是否是菜单
	 * @return
	 */
	boolean isMenu();
}
