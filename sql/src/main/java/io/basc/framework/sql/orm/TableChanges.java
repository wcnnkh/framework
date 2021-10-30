package io.basc.framework.sql.orm;

import io.basc.framework.mapper.Field;
import io.basc.framework.util.CollectionUtils;

import java.util.Collection;
import java.util.Collections;

/**
 * 获取表变更
 * 
 * @author shuchaowen
 *
 */
public class TableChanges {
	private Collection<String> deleteColumns;
	private Collection<Field> addColumns;

	@SuppressWarnings("unchecked")
	public TableChanges(Collection<String> deleteColumns, Collection<Field> addColumns) {
		this.deleteColumns = CollectionUtils.isEmpty(deleteColumns) ? Collections.EMPTY_LIST
				: Collections.unmodifiableCollection(deleteColumns);
		this.addColumns = CollectionUtils.isEmpty(addColumns) ? Collections.EMPTY_LIST
				: Collections.unmodifiableCollection(addColumns);
	}

	/**
	 * 获取删除的字段
	 * 
	 * @return
	 */
	public Collection<String> getDeleteColumns() {
		return deleteColumns;
	}

	/**
	 * 获取添加的字段
	 * 
	 * @return
	 */
	public Collection<Field> getAddColumnss() {
		return addColumns;
	}
}
