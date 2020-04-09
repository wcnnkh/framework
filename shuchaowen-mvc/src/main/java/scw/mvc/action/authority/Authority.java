package scw.mvc.action.authority;

import java.util.Map;

import scw.mvc.action.Action;


public interface Authority {
	String getId();

	String getParentId();

	String getName();
	
	Map<String, String> getAttributeMap();
	
	/**
	 * 返回受管理的action
	 * @return
	 */
	Action getAction();
 }
