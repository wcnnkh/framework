package scw.orm.sql;

import java.util.Collection;

import scw.orm.MappingContext;

/**
 * 获取表变更
 * 
 * @author shuchaowen
 *
 */
public interface TableChange {
	/**
	 * 获取删除的字段
	 * 
	 * @return
	 */
	Collection<String> getDeleteNames();

	/**
	 * 获取添加的字段
	 * 
	 * @return
	 */
	Collection<MappingContext> getAddMappingContexts();
}
