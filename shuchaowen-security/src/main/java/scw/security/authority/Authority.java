package scw.security.authority;

import java.util.Map;

public interface Authority {
	String getId();

	String getParentId();

	String getName();
	
	/**
	 * 是否是菜单
	 * @return
	 */
	boolean isMenu();

	Map<String, String> getAttributeMap();
}
